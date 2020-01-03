package com.hexminds.sparrow.lexer;

import com.hexminds.sparrow.App;
import com.hexminds.sparrow.lexer.ConcreteToken;
import com.hexminds.sparrow.lexer.ConcreteTokenList;
import com.hexminds.sparrow.lexer.Lexer;
import com.hexminds.sparrow.lexer.Scanner;
import com.hexminds.sparrow.parser.Parser;
import com.hexminds.util.StringUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Mill {
    public static boolean traverseDir(Path path, boolean recursive, boolean continuous, boolean warningAsError, boolean lex, boolean ast) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (recursive)
                        return FileVisitResult.CONTINUE;
                    else {
                        if (dir.compareTo(path) == 0)
                            return FileVisitResult.CONTINUE;
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (!attrs.isRegularFile())
                        return FileVisitResult.CONTINUE;
                    if (!file.getFileName().toString().endsWith(App.SRC_FILE_SUFFIX))
                        return FileVisitResult.CONTINUE;

                    if (compileFile(file, warningAsError, lex, ast) || !continuous)
                        return FileVisitResult.CONTINUE;
                    return FileVisitResult.TERMINATE;
                }
            });
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean compileFile(Path path, boolean warningAsError, boolean lex, boolean ast) {
        Scanner scanner;
        Lexer lexer;
        String str;
        String fileName;
        int index;
        String baseName;
        Path lexFile;
        FileWriter fw;
        BufferedWriter bw;
        int result;
        Parser parser;

        if (path == null || !Files.isRegularFile(path))
            return false;

        str = String.format("[信息] 编译：%s", path.toString());
        System.out.println(str);
        scanner = null;
        index = 0;
        try {

            scanner = new Scanner(path);
            if (!scanner.open())
                return false;
            lexer = new Lexer(scanner);
            fileName = path.getFileName().toString();
            index = fileName.lastIndexOf(".");
            if (index == -1)
                baseName = fileName;
            else
                baseName = fileName.substring(0, index);
            if (lex) {
                baseName += App.LEX_FILE_SUFFIX;
                lexFile = Paths.get(path.getParent().toString(), baseName);
                fw = new FileWriter(lexFile.toFile());
                bw = new BufferedWriter(fw);
                while (true) {
                    result = lexer.nextToken();
                    if (result == -1)
                        break;
                    //bw.write(URLEncoder.encode(lexer.getToken().toString(), "UTF-8"));
                    String s = lexer.getToken().toString();
                    bw.write(s);
                    bw.newLine();
                }
                bw.close();
            }

            if (ast) {
                fileName = path.getFileName().toString();
                index = fileName.lastIndexOf(".");
                if (index == -1)
                    baseName = fileName;
                else
                    baseName = fileName.substring(0, index);
                baseName += App.AST_FILE_SUFFIX;
                lexFile = Paths.get(path.getParent().toString(), baseName);
                fw = new FileWriter(lexFile.toFile());
                bw = new BufferedWriter(fw);
                scanner = new Scanner(path);
                if (!scanner.open())
                    return false;
                lexer = new Lexer(scanner);
                parser = new Parser(lexer, bw);
                parser.parse();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        } finally {
            if (scanner != null)
                scanner.close();
        }

//        if (ret)
//            System.out.println("编译完毕");
//        else
//            System.err.println("编译失败");
        return true;
    }
}
