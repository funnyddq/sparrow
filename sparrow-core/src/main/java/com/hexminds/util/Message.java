package com.hexminds.util;

public class Message {
    public static void error(String message) {
        System.err.println(String.format("[错误] %s", message));
    }

    public static void warn(String message) {
        System.out.println(String.format("[警告] %s", message));
    }

    public static void info(String message) {
        System.out.println(String.format("[信息] %s", message));
    }
}
