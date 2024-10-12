package org.app.model;

public record NameChangeRequest(
        String name, String email, String language
) {
}
