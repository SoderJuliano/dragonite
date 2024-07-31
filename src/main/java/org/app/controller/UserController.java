package org.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.app.model.Login;
import org.app.model.UserRecord;
import org.app.model.common.DefaultAnswer;
import org.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Tag(name = "custom-cv-online`s User", description = "User`s endpoints of https://custom-cv-online.netlify.app.")
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH})
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultAnswer> getUser(@PathVariable(name = "id") String id) {
        return ResponseEntity.status(200).body(new DefaultAnswer(userService.getUser(id)));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultAnswer> newUser(@RequestBody UserRecord userRecord) throws BadRequestException {
        return ResponseEntity.status(201).body(new DefaultAnswer(userService.newUser(userRecord)));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultAnswer> updateFullUser(@RequestBody UserRecord userRecord) throws BadRequestException {
        return ResponseEntity.status(200).body(new DefaultAnswer(userService.updateUser(userRecord)));
    }

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultAnswer> login(@RequestBody Login login) throws BadRequestException {
        return  ResponseEntity.status(200).body(new DefaultAnswer(userService.login(login)));
    }

    @PostMapping(path = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultAnswer> newLogin(@RequestBody Login login) throws BadRequestException, UnsupportedEncodingException {
        return  ResponseEntity.status(200).body(new DefaultAnswer(userService.newLogin(login)));
    }

    @PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateName(@RequestParam String name, @RequestParam String email) {
        if(userService.userExistByNameAndEmail(name, email)) {
            return ResponseEntity.status(100).build();
        }
        userService.updateUserName(name, email);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "/activate/{id}/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultAnswer> activateUser(@PathVariable String id, @PathVariable String code) throws BadRequestException {
        return ResponseEntity.status(200).body(userService.activateUserById(id, code));
    }

    @PutMapping(path = "/recover/{id}/password")
    public ResponseEntity<DefaultAnswer> recoverPassword(@PathVariable String id) {
        return ResponseEntity.status(200).body(userService.recoverPassword(id));
    }
}
