package org.app.utils;

import static org.app.config.EmojiParser.parseEmojis;

public class LocalLog {

    public static void log(String message) {
        System.out.println(parseEmojis(message));
    }

    public static void log(String message, Object... args) {
        System.out.printf(parseEmojis(message), args);
    }

    public static void log(String message, Throwable throwable) {
        System.err.println(parseEmojis(message) + "\n" + throwable.getMessage());
    }

    public static void log(String message, Throwable throwable, Object... args) {
        newLine();
        System.err.printf(parseEmojis(message), args);
        newLine();
        System.err.println(throwable.getMessage());
    }

    public static void log(Throwable throwable) {
        throwable.printStackTrace();
    }

    public static void logErr(String message) {
        System.err.println(parseEmojis(message));
    }

    public static void newLine() {
        System.out.print("\n");
    }
}
