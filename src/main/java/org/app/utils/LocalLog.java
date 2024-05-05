package org.app.utils;

public class LocalLog {

    public static void log(String message) {
        System.out.println(message);
    }

    public static void log(String message, Object... args) {
        System.out.printf(message, args);
    }

    public static void log(String message, Throwable throwable) {
        System.err.println(message);
        throwable.printStackTrace();
    }

    public static void log(String message, Throwable throwable, Object... args) {
        System.err.printf(message, args);
        throwable.printStackTrace();
    }

    public static void log(Throwable throwable) {
        throwable.printStackTrace();
    }

    public static void logErr(String message) {
        System.err.println(message);
    }
}
