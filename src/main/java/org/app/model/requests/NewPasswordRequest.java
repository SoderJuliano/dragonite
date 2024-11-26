package org.app.model.requests;

public record NewPasswordRequest(String id,
                                 String password,
                                 String token,
                                 String email,
                                 String language
                                ) {}
