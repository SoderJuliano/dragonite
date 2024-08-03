package org.app.services.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.coyote.BadRequestException;
import org.app.Exceptions.NoPasswordException;
import org.app.Exceptions.NotFoundException;
import org.app.model.Login;
import org.app.model.UserRecord;
import org.app.model.common.DefaultAnswer;
import org.app.model.entity.User;
import org.app.repository.LoginRepository;
import org.app.repository.UserRepository;
import org.app.services.UserService;
import org.app.utils.LocalLog;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.app.utils.GenericMapper.mapFields;
import static org.app.utils.LocalLog.log;
import static org.app.utils.LocalLog.logErr;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final LoginRepository loginRepository;

    private final TwoStepServiceImpl twoStepService;

    public UserServiceImpl(UserRepository userRepository, LoginRepository loginRepository, TwoStepServiceImpl twoStepService) {
        this.userRepository = userRepository;
        this.loginRepository = loginRepository;
        this.twoStepService = twoStepService;
    }

    public User newUser(UserRecord userRecord) throws BadRequestException {
        if(userRecord.contact() == null || userRecord.contact().email().isEmpty()) {
            logErr(":virus no email found in the payload");
            throw new IllegalArgumentException("Must have at list one email to save data into database");
        }
        LocalLog.log(":star New user request for " + userRecord.contact().email());
        if(userRecord.name().isEmpty()) {
            logErr(":virus no name found in the payload for "+userRecord.contact().email());
            throw new IllegalArgumentException("Must have a name");
        }
        User newUser = new User();
        try {
            userRepository.findByNameAndAnyEmail(userRecord.name(), userRecord.contact().email()).ifPresent(u -> {
                logErr(":negative This user already exist");
                throw new IllegalArgumentException("User already exists");
            });
            newUser = userRepository.insert((User) mapFields(newUser, userRecord));
        }catch (Exception exception) {
            logErr(":skull Probably duplicated _id");
            throw new BadRequestException("Can't create new user with those informations");
        }

        LocalLog.log(":positive User created for " + userRecord.contact().email());
        return newUser;
    }

    @Override
    public User getUser(String id) {
        return getUserbyId(id);
    }

    @Override
    public User updateUser(UserRecord userRecord) throws BadRequestException {
        User userToUpdate = userRepository.findByNameAndAnyEmail(userRecord.name(), userRecord.contact().email())
                .orElseThrow(
                        () -> {
                            logErr(":negative User not found during update. User's name: " + userRecord.name() + " user's email: " +
                                    userRecord.contact().email().get(0));
                            return new NotFoundException("User not found");
                        }
                );

        if(!userToUpdate.isActived()) {
            logErr(":lock Tried update user without confirm email account for email "+userRecord.contact().email());
            throw new BadRequestException("User did not confirm email account");
        }
        userToUpdate.setName(userRecord.name());
        userToUpdate.setProfession(userRecord.profession());
        userToUpdate.setResume(userRecord.resume());
        userToUpdate.setCompetence(userRecord.competence());
        userToUpdate.setSocial(userRecord.social());
        userToUpdate.setGrade(userRecord.grade());
        userToUpdate.setAbility(userRecord.ability());
        userToUpdate.setAvatarImg(userRecord.avatarImg());
        userToUpdate.setRealImg(userRecord.realImg());
        userToUpdate.setContact(userRecord.contact());

        LocalLog.log(":positive Updating user, name: " + userToUpdate.getName() + ", email: " + userToUpdate.getContact().email().get(0));
        return userRepository.save(userToUpdate);
    }

    @Override
    public User login(Login login) throws BadRequestException {
        LocalLog.log(":loz Trying login for " + login.email());

        List<Login> logins;

        if(login.userId().isBlank()) {
            logins = loginRepository.findByEmailAndPassword(login.email(), login.password());
        }else {
            logins = loginRepository.findByUserIdAndPasswordAndEmail(login.userId(), login.password(), login.email());
        }

        boolean hasActivationCode = userRepository.existsByActivationCodeAndId(login.password(), login.userId());

        if(hasActivationCode) {
            log(":laugh User loged in with activation code " + login.email());
            return userRepository.findById(login.userId()).orElseThrow(() -> {
                logErr(":warning Login failed for user " + login.email() + ". Data not found in users collection.");
                return new BadRequestException("Can't do login");
            });
        }

        boolean userExists = userRepository.existsById(login.userId());

        if (logins.isEmpty() && !userExists) {
            logErr(":negative Login not found for user " + login.email());
            throw new BadRequestException("No matching user found.");
        }else if (logins.isEmpty()) {
            log(":laugh The user exist but has no password");
            throw new NoPasswordException("Need register a password first");
        }
        else {
            LocalLog.log(":positive Login sucessfully for user " + login.email());
        }

        Login foundLogin = logins.get(0);

        User user = userRepository.findById(foundLogin.userId()).orElseThrow(() -> {
            logErr(":warning Login failed for user " + login.email() + ". Data not found in users collection.");
            return new BadRequestException("Can't do login");
        });

        if(!user.isActived()) {
            logErr(":lock Tried login without confirm email account for email "+user.getContact().email());
            throw new BadRequestException("User did not confirm email account");
        }

        return user;
    }

    @Override
    public Login newLogin(Login login) throws BadRequestException, UnsupportedEncodingException {
        User user = userRepository.findById(login.userId()).orElseThrow(() -> {
            logErr(":negative User do not exist for " + login.email());
            return new BadRequestException("Can't do login");
        });
        Login newLogin = loginRepository.insert(
                new Login(
                        login.email(),
                        login.password(),
                        login.userId(),
                        LocalDateTime.now(),
                        LocalDateTime.now()));

        LocalLog.log(":positive New login created for " + login.email());
        String token = UUID.randomUUID().toString();
        String key = login.email()+login.userId();

        twoStepService.sendEmail(login.email(), token, "[en]Your confirmation token/[pt]Código de confirmação");
        twoStepService.sendMessage(login.email(), "[en]We've sent a confirmation code to your email." +
                        "[pt]Enviamos um código de confirmação para seu email. E-mail: "+login.email(),
                "Please confirm your account", key);
        log(":receive_email Sent confirmation account email and message to user "+login.email());

        user.setActivationCode(token);
        user.setActived(false);
        userRepository.save(user);

        return newLogin;
    }

    @Override
    public void updateUserName(String name, String email) {
        Optional<User> userOptional = userRepository.findFirstByEmail(email);
        if (userOptional.isEmpty()) {
            logErr(":negative User not found for email "+email);
            throw new NotFoundException("User not found");
        }
        userOptional.get().setName(name);
        userRepository.save(userOptional.get());
        LocalLog.log(":positive User's name updated for email "+email);
    }

    @Override
    public boolean userExistByNameAndEmail(String name, String email) {
        Optional<User> userOptional = userRepository.findByNameAndAnyEmail(name, List.of(email));
        return userOptional.isPresent();
    }

    @Override
    public DefaultAnswer activateUserById(String id, String code) throws BadRequestException {
        User user = getUserbyId(id);
        if(user.isActived() && !Objects.equals(user.getActivationCode(), code)) {
            logErr(":lock User can't be actived due conditions, actived: "+user.isActived()+" code mathes: "
                    +Objects.equals(user.getActivationCode(), code));
            throw new BadRequestException("User cant be actived");
        }
        user.setActived(true);
        user.setActivationCode(null);
        return new DefaultAnswer(userRepository.save(user));
    }

    @Override
    public DefaultAnswer recoverPassword(String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty() || optionalUser.get().getContact().email().isEmpty()) {
            logErr(":negative User not found for id "+ id);
            throw new NoPasswordException("User cannot recover password");
        }

        String newPasswordToken = RandomStringUtils.randomAlphanumeric(10);
        String email = optionalUser.get().getContact().email().get(0);
        String newMessage = "Dear User,<br><br>" +
        "We have received a request to reset your password. Please follow the instructions below to complete the process:<br><br>" +
        "1. Click on the following link to reset your password:<br><a href=\"https://custom-cv-online.netlify.app/recover/password?newPasswordToken={{newPasswordToken}}\">Reset Password</a><br><br>" +
        "2. If you prefer, you can also log in with the token provided below and change your password later in the loginList preferences section:<br><br>" +
        "Token: {{newPasswordToken}}<br><br>" +
        "If you did not request a password reset, please ignore this email and your password will remain unchanged.<br><br>" +
        "Best regards,<br>" +
        "The Custom CV Online Team";

        String filledMessage = newMessage.replace("{{newPasswordToken}}", newPasswordToken);
        boolean success = twoStepService.sendHtmlEmail(email, "Reset Password", filledMessage);
        if(!success) {
            logErr(":lock Failed to send password reset email to user "+email);
            throw new org.app.Exceptions.BadRequestException("Cannot reset password");
        }
        optionalUser.get().setActivationCode(newPasswordToken);
        userRepository.save(optionalUser.get());
        log(":smile Sent password reset email to user "+email);
        return new DefaultAnswer("New password reset sent to e-mail successfully, and token saved to reset password");
    }

    @Override
    public DefaultAnswer requestDelete(String id) {
        log(":trash A request to delete data from id " + id + "begun");
        List<Login> logins = loginRepository.findByUserId(id);
        
        if (logins.isEmpty()) {
            logErr(":negative Login not found for user id " + id);
            throw new org.app.Exceptions.BadRequestException("No matching user found.");
        }
        
        Login login = logins.get(0);
        String key = login.email()+login.userId();

        Optional<User> optionalUser = userRepository.findById(id);
        User user = null;
        if (optionalUser.isEmpty() || optionalUser.get().getContact().email().isEmpty()) {
            logErr(":negative User not found for id "+ id);
            throw new NoPasswordException("User cannot recover password");
        }else {
            user = optionalUser.get();
        }

        String token = RandomStringUtils.randomAlphanumeric(10);
        user.setDeteToken(token);
        userRepository.save(user);

        boolean success = twoStepService.sendMessage(
            login.email(), 
            "Copie e cole esse token no campo do poup up/Copy and paste this token in the poup up field: "+token,
            "Token de confirmação/Confirmation token", 
            key
        );

        if(!success) {
            logErr(":lock Failed to send delete token to user "+login.email());
            throw new org.app.Exceptions.BadRequestException("Cannot delete account");
        }

        return new DefaultAnswer("Deletion token saved");
    }

    public DefaultAnswer doRequestDelete(String id, String token) {
        log(":trash Started delete user id "+id);
        
        Optional<User> optionalUser = userRepository.findById(id);
        User user = null;
        
        if (optionalUser.isEmpty() || optionalUser.get().getContact().email().isEmpty()) {
            logErr(":negative User not found for id "+ id);
            throw new NoPasswordException("User cannot recover password");
        }else {
            user = optionalUser.get();
        }

        if (!user.equalsDeleteToken(token)) {
            logErr(":lock Tryied delete user (id: " + id + ") with an invalid token");
            throw new org.app.Exceptions.BadRequestException("Invalid token");
        }

        userRepository.deleteById(id);
        log(":positive Deleted user "+id);
        
        return new DefaultAnswer();
    }

    private User getUserbyId(String id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            logErr(":negative User not found for id "+ id);
            throw new NotFoundException("User not found");
        }
        return userOptional.get();
    }
}
