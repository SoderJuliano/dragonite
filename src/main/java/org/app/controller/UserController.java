package org.app.controller;

import org.app.model.User;
import org.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public User getUser( @PathVariable(name = "id") String id) {
        return userService.getUser(id);
    }

    @PostMapping()
    public User newUser(User user) {
        return userService.newUser(user);
    }
}
