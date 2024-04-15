package org.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.app.model.User;
import org.app.model.common.DefaultAnswer;
import org.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "custom-cv-online`s User", description = "User`s endpoints of https://custom-cv-online.netlify.app.")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultAnswer> getUser(@PathVariable(name = "id") String id) {
        return ResponseEntity.status(200).body(new DefaultAnswer(userService.getUser(id)));
    }

    @PostMapping(path ="/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultAnswer> newUser(@RequestBody User user) {
        return ResponseEntity.status(201).body(new DefaultAnswer(userService.newUser(user)));
    }
}
