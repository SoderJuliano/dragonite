package org.app.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "login")
public record Login(String email, String password, String userId, @CreatedDate LocalDateTime firstLogin, @LastModifiedDate LocalDateTime lastLogin) {
}
