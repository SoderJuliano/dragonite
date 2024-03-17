package org.app.services;

import org.app.model.User;

public interface UserService {

    public User newUser(User user);

    public User getUser(String id);
}
