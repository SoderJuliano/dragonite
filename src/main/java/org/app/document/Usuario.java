package org.app.document;

import java.util.List;

public record Usuario(
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