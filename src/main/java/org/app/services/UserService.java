package org.app.services;

import org.apache.coyote.BadRequestException;
import org.app.model.Login;
import org.app.model.UserRecord;
import org.app.model.common.DefaultAnswer;
import org.app.model.entity.User;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

public interface UserService {

    public User newUser(UserRecord userRecord) throws BadRequestException;

    public User getUser(String id);

    public User updateUser(UserRecord userRecord) throws BadRequestException;

    public User login(Login login) throws BadRequestException;

    public Login newLogin(Login login) throws BadRequestException, UnsupportedEncodingException;

    public void updateUserName(String name, String email, String language);

    public boolean userExistByNameAndEmail(String name, String email, String language);

    public DefaultAnswer activateUserById(String id, String code, String email, String language);

    public DefaultAnswer recoverPassword(String id);

    public DefaultAnswer requestDelete(String id, String email, String language);

    public DefaultAnswer doRequestDelete(String id, String token);

    DefaultAnswer setPassword(String id, String password, String token);
}
