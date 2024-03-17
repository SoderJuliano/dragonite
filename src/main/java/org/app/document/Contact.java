package org.app.document;

import java.util.List;

public record Contact(
        String email,
        List<String> emails,
        String phone,
        Address address
) {}

record Address(String country, String state, String city, String street, String number, String district) {
}