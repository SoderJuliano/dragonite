package org.app.services;

import org.apache.coyote.BadRequestException;
import org.app.model.Login;
import org.app.model.UserRecord;
import org.app.model.entity.User;

public interface UserService {

    public User newUser(UserRecord userRecord) throws BadRequestException;

    public User getUser(String id);

    public User updateUser(UserRecord userRecord);

    public User login(Login login) throws BadRequestException;

    public Login newLogin(Login login) throws BadRequestException;

    public void updateUserName(String name, String email);
}
