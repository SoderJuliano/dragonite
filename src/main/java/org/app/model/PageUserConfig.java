package org.app.model;

import com.mongodb.lang.NonNull;

import static org.app.utils.Commons.isEmpty;
import static org.app.utils.Commons.notEmpty;


public record PageUserConfig(String language, String imageURL, Integer template, String font, String fontSize,
                             String fontSizeTitles, String fontColor, String sideColor, String mainColor, @NonNull String userId) {

    public PageUserConfig {
        template = template > 0 ? template : 1;
        language = notEmpty(language) ? language : "pt-br";
        mainColor = notEmpty(mainColor) ? mainColor : "#87CEEB";
        sideColor = notEmpty(sideColor) ? sideColor : "#B0C4DE";
        fontColor = notEmpty(fontColor) ? fontColor : "black";
        fontSizeTitles = notEmpty(fontSizeTitles) ? fontSizeTitles : "17px";
        fontSize = notEmpty(fontSize) ? fontSize : "15px";
        font = notEmpty(font) ? font : "Oswald";
    }

    public PageUserConfig(String userId) {
        this(null, null, null, null, null, null, null, null, null, userId);
    }
}
