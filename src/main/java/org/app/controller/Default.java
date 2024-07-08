package org.app.controller;

import org.app.config.EmojiParser;
import org.app.utils.LocalLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.app.config.EmojiParser.parseEmojis;

@RestController
public class Default {

    @GetMapping("/ping")
    public String ping() {
        LocalLog.log("ping...Pong" + ":earth");
        return EmojiParser.parseEmojis("Pong :smile");
    }
}
