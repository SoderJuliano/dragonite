package org.app.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public record User(
        String name,
        String profession,
        String resume,
        List<String> competence,
        List<String> social,
        List<String> grade,
        @JsonAlias("hability")
        String ability,
        String avatarImg,
        String realImg,
        Contact contact
){}