package org.app.services;

import org.app.model.UserRecord;
import org.app.model.entity.User;

public interface UserService {

    public User newUser(UserRecord userRecord);

    public User getUser(String id);

    public User updateUser(UserRecord userRecord);
}
