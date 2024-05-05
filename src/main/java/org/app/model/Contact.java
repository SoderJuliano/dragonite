package org.app.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public record Contact(
        List<String> email,
        List<String> phone,
        @JsonAlias("adressObject")
        Address address,
        @JsonAlias({"adress", "address"})
        String adressAsString
) {}

record Address(String country, String state, String city, String street, String number, String district) {
}