package org.app.model;

public enum Language {
        ENGLISH("us-en"),
        PORTUGUESE("pt-br");

        private final String code;

        private Language(String code) {
                this.code = code;
        }

        public String getCode() {
                return code;
        }
}
