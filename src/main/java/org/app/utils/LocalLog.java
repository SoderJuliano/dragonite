package org.app.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.app.config.EmojiParser.parseEmojis;

public class LocalLog {

    public static void log(String message) {
        System.out.println(logTime() + parseEmojis(message));
    }

    public static void log(String message, Object... args) {
        System.out.printf(logTime() + parseEmojis(message), args);
    }

    public static void log(String message, Throwable throwable) {
        System.err.println(logTime() + parseEmojis(message) + "\n" + throwable.getMessage());
    }

    public static void log(String message, Throwable throwable, Object... args) {
        newLine();
        System.err.printf(logTime() + parseEmojis(message), args);
        newLine();
        System.err.println(logTime() + throwable.getMessage());
    }

    public static void log(Throwable throwable) {
        throwable.printStackTrace();
    }

    public static void logErr(String message) {
        System.err.println(logTime() + parseEmojis(message));
    }

    public static void newLine() {
        System.out.print("\n");
    }

    public static String dataTime() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy HH:mm:ss");
        return now.format(formatter);
    }

    public static String logTime() {
        return parseEmojis(":time [" + dataTime() + "] :time");
    }
}
