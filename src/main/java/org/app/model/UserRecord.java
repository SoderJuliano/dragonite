package org.app.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public record UserRecord(
        @Id String _id,
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
        Contact contact,
        ArrayList<String> otherInfos,
        OtherExperiencies otherExperiencies
){
        public UserRecord() {
                this(null, null, null, null, null, null, null, null, null, null, null, null, null);
        }
}