package org.app.services.impl;

import com.mongodb.DuplicateKeyException;
import org.apache.coyote.BadRequestException;
import org.app.Exceptions.NotFoundException;
import org.app.model.Login;
import org.app.model.UserRecord;
import org.app.model.entity.User;
import org.app.repository.LoginRepository;
import org.app.repository.UserRepository;
import org.app.services.UserService;
import org.app.utils.LocalLog;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.app.utils.Commons.isNull;
import static org.app.utils.GenericMapper.mapFields;
import static org.app.utils.LocalLog.newLine;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final LoginRepository loginRepository;

    public UserServiceImpl(UserRepository userRepository, LoginRepository loginRepository) {
        this.userRepository = userRepository;
        this.loginRepository = loginRepository;
    }

    public User newUser(UserRecord userRecord) throws BadRequestException {
        if(userRecord.contact() == null || userRecord.contact().email().isEmpty()) {
            LocalLog.logErr(":virus no email found in the payload");
            throw new IllegalArgumentException("Must have at list one email to save data into database");
        }
        LocalLog.log(":star New user request for " + userRecord.contact().email());
        if(userRecord.name().isEmpty()) {
            LocalLog.logErr(":virus no name found in the payload for "+userRecord.contact().email());
            throw new IllegalArgumentException("Must have a name");
        }
        User newUser = new User();
        try {
            userRepository.findByNameAndAnyEmail(userRecord.name(), userRecord.contact().email()).ifPresent(u -> {
                LocalLog.logErr(":negative This user already exist");
                throw new IllegalArgumentException("User already exists");
            });
            newUser = userRepository.insert((User) mapFields(newUser, userRecord));
        }catch (Exception exception) {
            LocalLog.logErr(":skull Probably duplicated _id");
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
    public User updateUser(UserRecord userRecord) {
        User userToUpdate = userRepository.findByNameAndAnyEmail(userRecord.name(), userRecord.contact().email())
                .orElseThrow(
                        () -> {
                            System.out.println("User not found");
                            LocalLog.logErr("User not found during update. User's name: " + userRecord.name() + " user's email: " +
                                    userRecord.contact().email().get(0));
                            return new NotFoundException("User not found");
                        }
                );

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

        LocalLog.log("Updating user, name: " + userToUpdate.getName() + ", email: " + userToUpdate.getContact().email().get(0));
        return userRepository.save(userToUpdate);
    }

    @Override
    public User login(Login login) throws BadRequestException {
        LocalLog.log("Trying login for " + login.email());

        List<Login> logins;

        if(login.userId().isBlank()) {
            logins = loginRepository.findByEmailAndPassword(login.email(), login.password());
        }else {
            logins = loginRepository.findByUserIdAndPasswordAndEmail(login.userId(), login.password(), login.email());
        }

        if (logins.isEmpty()) {
            LocalLog.logErr("Login not found for user " + login.email());
            throw new BadRequestException("No matching user found.");
        }else {
            LocalLog.log("Login sucessfully for user " + login.email());
        }

        Login foundLogin = logins.get(0);

        return userRepository.findById(foundLogin.userId()).orElseThrow(() -> {
            LocalLog.logErr("Login failed for user " + login.email() + ". Data not found in users collection.");
            return new BadRequestException("Can't do login");
        });
    }

    @Override
    public Login newLogin(Login login) throws BadRequestException {
        userRepository.findById(login.userId()).orElseThrow(() -> {
            LocalLog.logErr("User do not exist for " + login.email());
            return new BadRequestException("Can't do login");
        });
        return loginRepository.insert(
                new Login(
                        login.email(),
                        login.password(),
                        login.userId(),
                        LocalDateTime.now(),
                        LocalDateTime.now()));
    }

    @Override
    public void updateUserName(String name, String email) {
        Optional<User> userOptional = userRepository.findFirstByEmail(email);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        userOptional.get().setName(name);
        userRepository.save(userOptional.get());
    }


    private User getUserbyId(String id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        return userOptional.get();
    }
}
