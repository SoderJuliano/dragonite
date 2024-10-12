package org.app.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "login")
public record Login(
        @Id ObjectId _id,
        String email,
        String password,
        String userId,
        @CreatedDate LocalDateTime firstLogin,
        @LastModifiedDate LocalDateTime lastLogin,
        String language
        ) {

    // Constructor with manual date setting
    public Login(ObjectId _id, String email, String password, String userId, LocalDateTime firstLogin,
                 LocalDateTime lastLogin, String language) {
        this._id = _id == null ? new ObjectId() : _id;
        this.email = email;
        this.password = password;
        this.userId = userId;
        this.firstLogin = firstLogin;
        this.lastLogin = lastLogin;
        this.language = language;
    }

    // Constructor without _id to let MongoDB generate it
    public Login(String email, String password, String userId, LocalDateTime firstLogin,
                 LocalDateTime lastLogin, String language) {
        this(new ObjectId(), email, password, userId, firstLogin, lastLogin, language);
    }
}
