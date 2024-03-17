package org.app.model;

import java.util.List;

public record User(
        String name,
        String profession,
        String resume,
        List<Object> competence,
        List<Object> social,
        List<Object> grade,
        String hability,
        String avatarImg,
        String realImg,
        Contact contact
){}