package org.app.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.app.config.EmojiParser.parseEmojis;

public class LocalLog {

    public static void log(String message) {
        System.out.println(parseEmojis(":time [" + dataTime() + "] ") + parseEmojis(message));
    }

    public static void log(String message, Object... args) {
        System.out.printf(parseEmojis(":time [" + dataTime() + "] ") + parseEmojis(message), args);
    }

    public static void log(String message, Throwable throwable) {
        System.err.println(parseEmojis(":time [" + dataTime() + "] ") + parseEmojis(message) + "\n" + throwable.getMessage());
    }

    public static void log(String message, Throwable throwable, Object... args) {
        newLine();
        System.err.printf(parseEmojis(":time [" + dataTime() + "] ") + parseEmojis(message), args);
        newLine();
        System.err.println("[" + dataTime() + "] " + throwable.getMessage());
    }

    public static void log(Throwable throwable) {
        throwable.printStackTrace();
    }

    public static void logErr(String message) {
        System.err.println(parseEmojis(":time [" + dataTime() + "] ") + parseEmojis(message));
    }

    public static void newLine() {
        System.out.print("\n");
    }

    public static String dataTime() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy HH:mm:ss");
        return now.format(formatter);
    }
}
