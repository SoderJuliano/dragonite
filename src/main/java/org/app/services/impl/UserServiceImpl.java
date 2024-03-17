package org.app.services.impl;

import org.app.Exceptions.NotFoundException;
import org.app.model.User;
import org.app.repository.UserRepository;
import org.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public User newUser(User user) {
        userRepository.findByNameAndAnyEmail(user.name(), user.contact().email()).ifPresent(u -> {
            throw new IllegalArgumentException("User already exists");
        });
        return userRepository.insert(user);
    }

    @Override
    public User getUser(String id) {
        return getUserbyId(id);
    }

    private User getUserbyId(String id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        return userOptional.get();
    }
}
