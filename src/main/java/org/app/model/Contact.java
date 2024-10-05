package org.app.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public record Contact(
        List<String> email,
        List<String> phone,
        AddressObject adressObject,
        String address
) {}

record AddressObject(String country, String state, String city, String street, String number, String district) {
}