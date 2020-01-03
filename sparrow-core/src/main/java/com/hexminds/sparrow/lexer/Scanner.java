package com.hexminds.sparrow.lexer;

import com.hexminds.sparrow.exception.IllegalCodePointException;
import com.hexminds.sparrow.exception.MalformedByteSequenceException;
import com.hexminds.sparrow.exception.ScannerException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Scanner implements CharStream {
    public static int DEFAULT_BUFFER_SIZE = 8192;
    public static int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
    protected Path file;
    protected int bufferSize;
    protected ByteBuffer buffer;
    protected ArrayList<Integer> cache;
    protected FileChannel channel;
    protected long read;
    protected long pos;
    protected boolean bomFound;

    public Scanner(Path file, int bufferSize) {
        this.file = file;
        if (bufferSize > 0 && bufferSize <= MAX_BUFFER_SIZE)
            this.bufferSize = bufferSize;
        else
            this.bufferSize = DEFAULT_BUFFER_SIZE;
    }

    public Scanner(Path file) {
        this(file, DEFAULT_BUFFER_SIZE);
    }

    public Path getFile(Path file) {
        return file;
    }

    public void setFile(Path file) {
        this.file = file;
        return;
    }

    public int getBufferSize(int bufferSize) {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return;
    }

    public long getRead() {
        return read;
    }

    public long getPos() {
        return pos;
    }

    public boolean open() throws IOException {
        if (!Files.isRegularFile(file))
            return false;
        buffer = ByteBuffer.allocate(bufferSize);
        buffer.flip();
        cache = null;
        channel = FileChannel.open(file);
        read = 0;
        pos = 0;
        bomFound = false;
        return true;
    }

    public int read() throws IOException, ScannerException {
        long start;
        int byte1;
        int byte2;
        int byte3;
        int byte4;
        int ret;
        String message;

        if (buffer == null)
            throw new NullPointerException();

        if (cache != null && cache.size() != 0) {
            read++;
            return cache.remove(cache.size() - 1);
        }

        start = pos;
        if (fill() == -1)
            return -1;
        byte1 = buffer.get() & 0xFF;
        pos++;
        if (byte1 >= 0 && byte1 < 0x80) {
            if (byte1 == 0) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s\" 代码点：%s 错误：源代码中不允许存在NUL字符",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase());
                throw new IllegalCodePointException(message);
            }
//            } else if (byte1 == 0x0D) {
//                message = String.format(
//                        "文件偏移：0x%s 字节序列：\"0x%s\" 代码点：%s 错误：源代码中不允许存在CR字符",
//                        String.format("%08x", start).toUpperCase(),
//                        String.format("%02x", byte1).toUpperCase(),
//                        String.format("%02x", byte1).toUpperCase());
//                throw new IllegalCodePointException(message);
//            }
            read++;
            return byte1;
        } else if ((byte1 & 0xE0) == 0xC0) {
            if (fill() == -1) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s\" 错误：期待两字节UTF-8字符的第二个字节，遇到EOF标记",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            byte2 = buffer.get() & 0xFF;
            pos++;
            if ((byte2 & 0xC0) != 0x80) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s\" 错误：无效的两字节UTF-8字符的第二个字节",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            ret = (byte1 & 0x1F) << 6 | (byte2 & 0x3F);
            if (ret < 0x80) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s\" 代码点：%s 错误：一字节UTF-8字符被编码为两个字节",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase(),
                        String.format("%08x", ret).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            read++;
            return ret;
        } else if ((byte1 & 0xF0) == 0xE0) {
            if (fill() == -1) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s\" 错误：期待三字节UTF-8字符的第二个字节，遇到EOF标记",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            byte2 = buffer.get() & 0xFF;
            pos++;
            if ((byte2 & 0xC0) != 0x80) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s\" 错误：无效的三字节UTF-8字符的第二个字节",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            if (fill() == -1) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s\" 错误：期待三字节UTF-8字符的第三个字节，遇到EOF标记",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            byte3 = buffer.get() & 0xFF;
            pos++;
            if ((byte3 & 0xC0) != 0x80) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s 0x%s\" 错误：无效的三字节UTF-8字符的第三个字节",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase(),
                        String.format("%02x", byte3).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            ret = (byte1 & 0x0F) << 12 | (byte2 & 0x3F) << 6 | (byte3 & 0x3F);
            if (ret < 0x80) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s 0x%s\" 代码点：%s 错误：一字节UTF-8字符被编码为三个字节",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase(),
                        String.format("%02x", byte3).toUpperCase(),
                        String.format("%08x", ret).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            if (ret < 0x0800) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s 0x%s\" 代码点：%s 错误：两字节UTF-8字符被编码为三个字节",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase(),
                        String.format("%02x", byte3).toUpperCase(),
                        String.format("%08x", ret).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            if (ret >= 0xD800 && ret <= 0xDFFF) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s 0x%s\" 代码点：0x%s 错误：无效的代理字符代码点",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase(),
                        String.format("%02x", byte3).toUpperCase(),
                        String.format("%08x", ret).toUpperCase());
                throw new IllegalCodePointException(message);
            }
            if (ret >= 0xFDD0 && ret <= 0xFDEF || (ret & 0xFFFE) == 0xFFFE) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s 0x%s\" 代码点：0x%s 错误：无效的UTF-8字符",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase(),
                        String.format("%02x", byte3).toUpperCase(),
                        String.format("%08x", ret).toUpperCase());
                throw new IllegalCodePointException(message);
            }
            if (ret != 0xFEFF) {
                read++;
                return ret;
            }
            if (read != 0 || bomFound) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s 0x%s\" 代码点：0x%s 错误：在错误的位置遇到BOM标记",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase(),
                        String.format("%02x", byte3).toUpperCase(),
                        String.format("%08x", ret).toUpperCase());
                throw new IllegalCodePointException(message);
            }
            bomFound = true;
            return read();
        } else if ((byte1 & 0xF8) == 0xF0) {
            if (fill() == -1) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s\" 错误：期待四字节UTF-8字符的第二个字节，遇到EOF标记",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            byte2 = buffer.get() & 0xFF;
            pos++;
            if ((byte2 & 0xC0) != 0x80) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s\" 错误：无效的四字节UTF-8字符的第二个字节",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            if (fill() == -1) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s\" 错误：期待四字节UTF-8字符的第三个字节，遇到EOF标记",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            byte3 = buffer.get() & 0xFF;
            pos++;
            if ((byte3 & 0xC0) != 0x80) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s 0x%s\" 错误：无效的四字节UTF-8字符的第三个字节",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase(),
                        String.format("%02x", byte3).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            if (fill() == -1) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s 0x%s\" 错误：期待四字节UTF-8字符的第四个字节，遇到EOF标记",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase(),
                        String.format("%02x", byte3).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            byte4 = buffer.get() & 0xFF;
            pos++;
            if ((byte4 & 0xC0) != 0x80) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s 0x%s 0x%s\" 错误：无效的四字节UTF-8字符的第四个字节",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase(),
                        String.format("%02x", byte3).toUpperCase(),
                        String.format("%02x", byte4).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            ret = (byte1 & 0x07) << 18 | (byte2 & 0x3F) << 12 | (byte3 & 0x3F) << 6 | (byte4 & 0x3F);
            if (ret < 0x80) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s 0x%s 0x%s\" 代码点：%s 错误：一字节UTF-8字符被编码为四个字节",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase(),
                        String.format("%02x", byte3).toUpperCase(),
                        String.format("%02x", byte4).toUpperCase(),
                        String.format("%08x", ret).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            if (ret < 0x0800) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s 0x%s 0x%s\" 代码点：%s 错误：两字节UTF-8字符被编码为四个字节",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase(),
                        String.format("%02x", byte3).toUpperCase(),
                        String.format("%02x", byte4).toUpperCase(),
                        String.format("%08x", ret).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            if (ret < 0x10000) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s 0x%s 0x%s\" 代码点：%s 错误：三字节UTF-8字符被编码为四个字节",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase(),
                        String.format("%02x", byte3).toUpperCase(),
                        String.format("%02x", byte4).toUpperCase(),
                        String.format("%08x", ret).toUpperCase());
                throw new MalformedByteSequenceException(message);
            }
            if ((ret & 0xFFFE) == 0xFFFE || ret > 0x10FFFF) {
                message = String.format(
                        "文件偏移：0x%s 字节序列：\"0x%s 0x%s 0x%s 0x%s\" 代码点：0x%s 错误：无效的UTF-8字符",
                        String.format("%08x", start).toUpperCase(),
                        String.format("%02x", byte1).toUpperCase(),
                        String.format("%02x", byte2).toUpperCase(),
                        String.format("%02x", byte3).toUpperCase(),
                        String.format("%02x", byte4).toUpperCase(),
                        String.format("%08x", ret).toUpperCase());
                throw new IllegalCodePointException(message);
            }
            read++;
            return ret;
        }
        message = String.format(
                "文件偏移：0x%s 字节序列：\"0x%s\" 错误：无效的UTF-8字符的第一个字节",
                String.format("%08x", start).toUpperCase(),
                String.format("%02x", byte1).toUpperCase());
        throw new MalformedByteSequenceException(message);
    }

    public void pushBack(int codePoint) {
        if (cache == null)
            cache = new ArrayList<>();
        cache.add(codePoint);
        read--;
        return;
    }

    public void pushBack(int[] codePoints) {
        if (cache == null)
            cache = new ArrayList<>();
        for (int i = codePoints.length - 1; i >= 0; i--) {
            cache.add(codePoints[i]);
            read--;
        }
        return;
    }

    public void pushBack(ArrayList<Integer> codePoints) {
        if (cache == null)
            cache = new ArrayList<>();
        for (int i = codePoints.size() - 1; i >= 0; i--) {
            cache.add(codePoints.get(i));
            read--;
        }
        return;
    }

    public void close() {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                return;
            } finally {
                channel = null;
            }
        }
        return;
    }

    private int fill() throws IOException {
        int read;

        if (!buffer.hasRemaining()) {
            buffer.clear();
            do {
                read = channel.read(buffer);
            } while (read == 0);
            buffer.flip();
            return read;
        }

        return 0;
    }
}
