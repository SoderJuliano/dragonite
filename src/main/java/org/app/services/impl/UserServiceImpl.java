package org.app.services.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.app.Exceptions.BadRequestException;
import org.app.Exceptions.NoPasswordException;
import org.app.Exceptions.NotFoundException;
import org.app.model.FrontHost;
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

import static org.app.utils.Commons.isEmpty;
import static org.app.utils.Commons.notEmpty;
import static org.app.utils.GenericMapper.mapFields;
import static org.app.utils.LocalLog.log;
import static org.app.utils.LocalLog.logErr;
import static org.app.utils.PasswordUtils.checkPassword;
import static org.app.utils.PasswordUtils.hashPassword;

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

    public User newUser(UserRecord userRecord) {
        if(userRecord.contact() == null || userRecord.contact().email().isEmpty()) {
            logErr(":virus no email found in the payload");
            throw new IllegalArgumentException("Must have at list one email to save data into database");
        }

        if(userRecord.language() == null) {
            logErr(":virus no language found in the payload");
            throw new IllegalArgumentException("Must have a language");
        }

        if(userRepository.existsByContactEmailAndLanguage(userRecord.contact().email().get(0),
                userRecord.language())) {
            logErr(":negative this email already exist in the database"+userRecord.contact().email().get(0));
            throw new IllegalArgumentException("Can not save those informations");
        }

        LocalLog.log(":star New user request for " + userRecord.contact().email());
        if(userRecord.name().isEmpty()) {
            logErr(":virus no name found in the payload for "+userRecord.contact().email());
            throw new IllegalArgumentException("Must have a name");
        }
        User newUser = new User();
        try {
            userRepository.findByAnyEmailAndLanguage(
                    userRecord.contact().email(),
                    userRecord.language()
            ).ifPresent(u -> {
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
        User userToUpdate = userRepository.findByAnyEmailAndLanguage(
                    userRecord.contact().email(),
                    userRecord.language()
                )
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

        mapFields(userToUpdate, userRecord);
        userToUpdate.setLastUpdatedToNow();

        LocalLog.log(":positive Updating user, name: " + userToUpdate.getName() + ", email: " + userToUpdate.getContact().email().get(0));
        return userRepository.save(userToUpdate);
    }

    @Override
    public User login(Login login) {
        LocalLog.log(":loz Trying login for " + login.email());

        List<Login> logins;
        logins = loginRepository.findByUserIdAndEmail(login.userId(), login.email());
        if(logins.isEmpty()) {
            logins = loginRepository.findByEmailAndLanguage(login.email(), login.language());
        }

        boolean isValidLogin = logins.stream()
                .anyMatch(l -> checkPassword(login.password(), l.password()));

        if (!isValidLogin) {
            throw new BadRequestException("Invalid email, password, or language.");
        }

        //Em caso de login por token, esse token já é um hash e vai vir no campo password
        boolean hasActivationCode = userRepository.existsByActivationCodeAndId(login.password(), login.userId());

        if(hasActivationCode) {
            log(":laugh User loged in with activation code " + login.email());
            return userRepository.findById(login.userId()).orElseThrow(() -> {
                logErr(":warning Login failed for user " + login.email() + ". Data not found in users collection.");
                return new BadRequestException("Can't do login");
            });
        }

        boolean userExists = userRepository.existsById(login.userId());
        boolean existsByEmailAndLanguage = userRepository.findFirstByEmailAndLanguage(login.email(), login.language()).isPresent();
        userExists = userExists || existsByEmailAndLanguage;

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

        User user = getUser(logins);

        if(!user.isActived()) {
            logErr(":lock Tried login without confirm email account for email "+user.getContact().email());
            throw new BadRequestException("User did not confirm email account");
        }

        return user;
    }

    @Override
    public Login newLogin(Login login) throws UnsupportedEncodingException {
        User user = userRepository.findById(login.userId()).orElseThrow(() -> {
            logErr(":negative User do not exist for " + login.email());
            return new BadRequestException("Can't do login");
        });

        Login newLogin = loginRepository.insert(
                new Login(
                        login.email(),
                        hashPassword(login.password()),
                        login.userId(),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        login.language()
                )
        );

        LocalLog.log(":positive New login created for " + login.email());

        sendConfirmationCode(login, user);

        return newLogin;
    }

    private void sendConfirmationCode(Login login, User user) {
        String token = UUID.randomUUID().toString();
        int numericCode = Math.abs(token.hashCode()) % 10_000_000;
        String sevenDigitCode = String.format("%07d", numericCode);

        String key = login.email()+ login.userId();

        twoStepService.sendEmail(login.email(), sevenDigitCode, "[en]Your confirmation token/[pt]Código de confirmação");
        twoStepService.sendMessage(login.email(), "[en]We've sent a confirmation code to your email." +
                        "[pt]Enviamos um código de confirmação para seu email. E-mail: "+ login.email(),
                "Please confirm your account", key);
        log(":receive_email Sent confirmation account email and message to user "+ login.email());

        user.setActivationCode(sevenDigitCode);
        user.setActived(false);
        userRepository.save(user);
    }

    @Override
    public void resendEmail(String email, String lang) {
        List<Login> login = loginRepository.findByEmailAndLanguage(email, lang);

        if(login.isEmpty()) {
            new BadRequestException("No login for "+email);
        }

        Optional<User> user = userRepository.findFirstByEmailAndLanguage(email, lang);

        if(!user.isPresent()) {
            new BadRequestException("No user for "+email);
        }

        sendConfirmationCode(login.get(0), user.get());
    }

    @Override
    public void updateUserName(String name, String email, String language) {
        Optional<User> userOptional = userRepository.findFirstByEmailAndLanguage(email, language);
        if (userOptional.isEmpty()) {
            logErr(":negative User not found for email "+email);
            throw new NotFoundException("User not found");
        }
        userOptional.get().setName(name);
        userRepository.save(userOptional.get());
        LocalLog.log(":positive User's name updated for email "+email);
    }

    @Override
    public boolean userExistByEmailAndLanguage(String name, String email, String language) {
        Optional<User> userOptional = userRepository.findByAnyEmailAndLanguage(List.of(email), language);
        return userOptional.isPresent();
    }

    @Override
    public DefaultAnswer activateUserById(String id, String code, String email, String language) {
        User user = null;

        if(isEmpty(id) || id.length() != 24) {
            log(":warning Invalid id "+id+ " will try recover user with email "+email);
            Optional<User> userByEmail = userRepository.findFirstByEmailAndLanguage(email, language);
            if(userByEmail.isPresent()) {
                log(":positive User found for email "+email);
                user = userByEmail.get();
            }
        }

        user = getUserbyId(id);

        if(user.isActived() && !Objects.equals(user.getActivationCode(), code)) {
            logErr(":lock User can't be actived due conditions, actived: "+user.isActived()+" code mathes: "
                    +Objects.equals(user.getActivationCode(), code));
            throw new BadRequestException("User cant be actived or already been activated");
        }
        user.setActived(true);
        user.setActivationCode(null);
        log(":star Activated account succesfully for user "+id);
        return new DefaultAnswer(userRepository.save(user));
    }

    @Override
    public DefaultAnswer recoverPassword(String id, String host) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty() || optionalUser.get().getContact().email().isEmpty()) {
            logErr(":negative User not found for id " + id);
            throw new NoPasswordException("User cannot recover password");
        }

        String newPasswordToken = RandomStringUtils.randomAlphanumeric(10);
        String email = optionalUser.get().getContact().email().get(0);

        // Verifica se o host é válido, caso contrário, usa o valor padrão
        String baseUrl = (host == null || host.isEmpty()) ? "https://custom-cv-online.netlify.app" : host;

        String newMessage = "Dear User,<br><br>" +
                "We have received a request to reset your password. Please follow the instructions below to complete the process:<br><br>" +
                "1. Click on the following link to reset your password:<br>" +
                "<a href=\"" + baseUrl + "/recover/password?newPasswordToken={{newPasswordToken}}\">Reset Password</a><br><br>" +
                "2. If you prefer, you can also log in with the token provided below and change your password later in the loginList preferences section:<br><br>" +
                "Token: {{newPasswordToken}}<br><br>" +
                "If you did not request a password reset, please ignore this email and your password will remain unchanged.<br><br>" +
                "Best regards,<br>" +
                "The Custom CV Online Team";

        String filledMessage = newMessage.replace("{{newPasswordToken}}", newPasswordToken);
        boolean success = twoStepService.sendHtmlEmail(email, "Reset Password", filledMessage);
        if (!success) {
            logErr(":lock Failed to send password reset email to user " + email);
            throw new BadRequestException("Cannot reset password");
        }

        optionalUser.get().setActivationCode(newPasswordToken);
        userRepository.save(optionalUser.get());
        log(":smile Sent password reset email to user " + email);
        return new DefaultAnswer("New password reset sent to e-mail successfully, and token saved to reset password");
    }

    @Override
    public DefaultAnswer requestDelete(String id, String email, String language) {
        log(":trash A request to delete data from id " + id + " begun");

        Optional<User> optionalUser = null;
        optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            log("warning user not found by id " + id + " will search user by email address " + email);
            optionalUser = userRepository.findFirstByEmailAndLanguage(email, language);
        } else if(optionalUser.get().getContact().email().isEmpty()) {
            logErr(":negative User not found for id "+ id +" and user does not has e-mail.");
            throw new NoPasswordException("User cannot recover password");
        }

        User user = optionalUser.get();

        String key = email+id;

        String token = RandomStringUtils.randomAlphanumeric(10);
        user.setDeteToken(token);
        userRepository.save(user);

        boolean success = twoStepService.sendMessage(
             email,
            "Copie e cole esse token no campo do poup up/Copy and paste this token in the poup up field: "+token,
            "Token de confirmação/Confirmation token", 
            key
        );

        boolean emailSetnt = twoStepService.sendEmail(
                email,
                "Copie e cole esse token no campo do poup up/Copy and paste this token in the poup up field: "+token,
                "Token de confirmação/Confirmation token"
                );

        if(!success && !emailSetnt) {
            logErr(":lock Failed to send delete token to user "+email);
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

        List<Login> logins = loginRepository.findByEmail(user.getContact().email());
        if(logins.isEmpty()) {
            logErr(":negative Login not found for user id " + id);
        }else {
            loginRepository.deleteByUserId(user.getId());
            log(":fire Login deleted for user " + user.getId());
        }

        userRepository.deleteById(id);
        log(":positive Deleted user "+id);
        
        return new DefaultAnswer();
    }

    /**
     * Sets a new password for the user associated with the given ID and token.
     *
     * @param id       The unique identifier of the user.
     * @param password The new password to be set.
     * @param token    The token used to verify the user's identity.
     * @param email
     * @return A {@link DefaultAnswer} object indicating the success of the operation.
     * @throws NotFoundException   If the user associated with the given ID does not exist.
     * @throws BadRequestException If the user associated with the given ID cannot change password due to invalid token.
     */
    @Override
    public DefaultAnswer setPassword(String id, String password, String token, String email, String language) {
        User user = null;
        if(notEmpty(id)) {
            user = userRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("User " + id + " does not exist"));
        }else {
            user = userRepository.findFirstByEmailAndLanguage(email, language)
                    .orElseThrow(() -> new NotFoundException("User " + email + " does not exist"));
        }

        if(!user.getActivationCode().equals(token)) {
            throw new BadRequestException("User " + id + " cannot change password");
        }
        Login login = loginRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("User " + id + " does not exist"));
        loginRepository.save(new Login(login._id(), login.email(), hashPassword(password), login.userId(), login.firstLogin(), login.lastLogin(), login.language()));
        log(":writing password changed for user " + user.getId());
        return new DefaultAnswer("Password changed");
    }

    @Override
    public DefaultAnswer recoverPasswordByEmail(String email, String language, FrontHost request) {
        User user = userRepository.findFirstByEmailAndLanguage(email, language).orElseThrow(() -> new NotFoundException("Not found user " + email));
        return recoverPassword(user.getId(), request.host());
    }

    @Override
    public void grantPremiumAccess(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found user for id " + id));
        user.setPremium(true);
        userRepository.save(user);
    }

    private User getUserbyId(String id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            logErr(":negative User not found for id "+ id);
            throw new NotFoundException("User not found");
        }
        return userOptional.get();
    }

    private User getUser(List<Login> logins) {
        User user = null;
        for (Login ilogin : logins) {
            user = userRepository.findById(ilogin.userId()).orElse(null);
            if (user != null) {
                break;
            }
        }

        if (user == null) {
            logErr(":warning Login failed for user " + logins.get(0).email() + ". Data not found in users collection.");
            throw new BadRequestException("Can't do login");
        }
        return user;
    }
}
