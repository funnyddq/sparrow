package com.hexminds.sparrow;

import com.hexminds.sparrow.lexer.Mill;
import com.hexminds.util.Message;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class App {
    public static final String APPLICATION_NAME = "sparrow-core";
    public static final String APPLICATION_VERSION = "0.0.1-SNAPSHOT";
    public static final String SRC_FILE_SUFFIX = ".spa";
    public static final String LEX_FILE_SUFFIX = ".lex";
    public static final String AST_FILE_SUFFIX = ".ast";

    @Option(name = "-h", aliases = "--help", usage = "显示帮助信息", handler = com.hexminds.sparrow.HideBooleanOptionHandler.class)
    private boolean help = false;

    @Option(name = "-v", aliases = "--version", usage = "显示版本信息", handler = com.hexminds.sparrow.HideBooleanOptionHandler.class)
    private boolean version = false;

    @Option(name = "-d", usage = "编译指定目录下的所有汇编源文件", metaVar = "directory")
    private String directory = null;

    @Option(name = "-r", usage = "递归目录", handler = com.hexminds.sparrow.HideBooleanOptionHandler.class)
    private boolean recursive = false;

    @Option(name = "-c", usage = "遇到错误继续执行", handler = com.hexminds.sparrow.HideBooleanOptionHandler.class)
    private boolean continuous = false;

    @Option(name = "-w", usage = "将警告当做错误处理", handler = com.hexminds.sparrow.HideBooleanOptionHandler.class)
    private boolean warningAsError = false;

    @Option(name = "--lex", usage = "输出词法分析结果", handler = com.hexminds.sparrow.HideBooleanOptionHandler.class)
    private boolean lex = false;

    @Option(name = "--ast", usage = "输出语法分析结果", handler = com.hexminds.sparrow.HideBooleanOptionHandler.class)
    private boolean ast = false;

    @Argument
    private ArrayList<String> arguments = new ArrayList<>();

    public static void main(String[] args) {
        new App().doMain(args);

    }

    public void doMain(String[] args) {
        CmdLineParser parser;
        Path path;

        parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            Message.error(e.getMessage());
            System.exit(1);
        }

        if (help) {
            showHelp(parser);
            return;
        }

        if (version) {
            showVersion();
            return;
        }

        if (directory == null && arguments.size() == 0) {
            Message.error("请指定目录或文件");
            System.exit(1);
        }
        if (directory != null && arguments.size() != 0) {
            Message.error("不能同时指定目录和文件");
            System.exit(1);
        }
        if (directory == null && recursive) {
            Message.error("只有在指定目录时才支持递归模式");
            System.exit(1);
        }

        if (directory != null) {
            path = Paths.get(directory);
            if (!Files.exists(path)) {
                Message.error(String.format("目录不存在：%s", path.toString()));
                System.exit(1);
            }
            if (!Files.isDirectory(path)) {
                Message.error(String.format("不是目录：%s", path.toString()));
                System.exit(1);
            }
            if (!Mill.traverseDir(path, recursive, continuous, warningAsError, lex, ast))
                System.exit(1);
        } else {
            for (String file : arguments) {
                path = Paths.get(file);
                if (!Files.exists(path)) {
                    Message.error(String.format("文件不存在：%s", path.toString()));
                    if (continuous)
                        continue;
                    System.exit(1);
                }
                if (!Files.isRegularFile(path)) {
                    Message.error(String.format("不是文件：%s", path.toString()));
                    if (continuous)
                        continue;
                    System.exit(1);
                }
                if (!path.toString().endsWith(SRC_FILE_SUFFIX)) {
                    Message.error(String.format("错误的文件类型：%s", path.toString()));
                    if (continuous)
                        continue;
                    System.exit(1);
                }
                if (!Mill.compileFile(path, warningAsError, lex, ast) && !continuous)
                    System.exit(1);
            }
        }

        return;
    }

    public static void showHelp(CmdLineParser parser) {
        if (parser == null)
            return;

        System.out.println("java -jar " + APPLICATION_NAME + "-" + APPLICATION_VERSION + ".jar [options...] arguments...");
        parser.printUsage(System.out);
        return;
    }

    public static void showVersion() {
        System.out.println(APPLICATION_NAME + " " + APPLICATION_VERSION);
        return;
    }
}
