package org.app.config;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

public class EmojiParser {
    private static final Map<String, String> emojiMap = new HashMap<>();

    static {
        // Using hexadecimal Unicode representations for emojis
        emojiMap.put("smile", "\uD83D\uDE0A");      // 😊
        emojiMap.put("laugh", "\uD83D\uDE02");      // 😂
        emojiMap.put("positive", "\uD83D\uDC4D");   // 👍
        emojiMap.put("wave", "\uD83D\uDC4B");       // 👋
        emojiMap.put("heart", "\u2764\uFE0F");      // ❤️
        emojiMap.put("star", "\uD83C\uDF1F");       // ⭐️
        emojiMap.put("fire", "\uD83D\uDD25");       // 🔥
        emojiMap.put("rocket", "\uD83D\uDE80");     // 🚀
        emojiMap.put("skull", "\uD83D\uDC80");      // 💀
        emojiMap.put("coffee", "\u2615");           // ☕
        emojiMap.put("negative", "\uD83D\uDC4E");   // 👎
        emojiMap.put("virus", "\uD83C\uDF0E");      // 🌎
        emojiMap.put("book", "\uD83D\uDCD6");       // 📖
        emojiMap.put("laptop", "\uD83D\uDCBB");     // 💻
        emojiMap.put("writing", "\uD83D\uDCDD");    // 📝
        emojiMap.put("uploading", "\uD83D\uDCE4");   // 📤
        emojiMap.put("downloading", "\uD83D\uDCE5");// 📥
        emojiMap.put("email", "\uD83D\uDCE7");      // 📧
        emojiMap.put("send_email", "\uD83D\uDCE8"); // 📩
        emojiMap.put("receive_email", "\uD83D\uDCE9"); // 📨
        emojiMap.put("watch", "\uD83D\uDD50");      // 🔔
        emojiMap.put("computer", "\uD83D\uDDE0");   // 🖥️
        emojiMap.put("trash", "\uD83D\uDDD1\uFE0F");// 🗑️
        emojiMap.put("calendar", "\uD83D\uDDD3\uFE0F"); // 📅
        emojiMap.put("files", "\uD83D\uDDC2\uFE0F");    // 📂
        emojiMap.put("stone_face", "\uD83D\uDDFF"); // 🗿
        emojiMap.put("stop", "\uD83D\uDEAB");       // ⛫
        emojiMap.put("market_car", "\uD83D\uDED2"); // 🛒
        emojiMap.put("bug", "\uD83D\uDC1E");        // 🐞
        emojiMap.put("spider_web", "\uD83D\uDD78");// 🕸️
        emojiMap.put("salt", "\uD83E\uDDC2");       // 🧂
        emojiMap.put("earth", "\uD83C\uDF0E");      // 🌎
        emojiMap.put("money_bag", "\uD83D\uDCB0");  // 💰
        emojiMap.put("dollar", "\uD83D\uDCB5");     // 💵
        emojiMap.put("credit_card", "\uD83D\uDCB3");// 💳
        emojiMap.put("currency_exchange", "\uD83D\uDCB1"); // 💱
        emojiMap.put("key", "\uD83D\uDD11");        // 🔑
        emojiMap.put("lock", "\uD83D\uDD12");       // 🔒
        emojiMap.put("wrench", "\uD83D\uDD27");     // 🔧
        emojiMap.put("hammer", "\uD83D\uDD28");     // 🔨
        emojiMap.put("receipt", "\uD83E\uDDFE");    // 🧾
        emojiMap.put("give_hands", "\uD83E\uDD1D"); // 🤝
        emojiMap.put("rock", "\uD83E\uDD1F");       // 🪨
        emojiMap.put("arrow_right", "\u27A1\uFE0F"); // ➡️
        emojiMap.put("go_down", "\u2B07\uFE0F");    // ⬇️
        emojiMap.put("loz", "\uD83D\uDC8E");        // 💎
        emojiMap.put("warning", "\u26A0\uFE0F");    // ⚠️
        emojiMap.put("time", "\u23F0");  // ⏰
    }

    public static String parseEmojis(String text) {
        StringBuilder result = new StringBuilder(text);

        // Iterating over the emoji map
        for (Map.Entry<String, String> entry : emojiMap.entrySet()) {
            String emojiCode = ":" + entry.getKey();
            String emoji = entry.getValue();

            // Replace all occurrences of emojiCode with emoji
            result = new StringBuilder(result.toString().replace(emojiCode, emoji));
        }

        return result.toString();
    }
}
