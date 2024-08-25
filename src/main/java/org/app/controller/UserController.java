package org.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.app.model.Login;
import org.app.model.UserRecord;
import org.app.model.common.DefaultAnswer;
import org.app.model.requests.NewPasswordRequest;
import org.app.services.UserService;
import org.app.utils.LocalLog;
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
            LocalLog.log(":stone_face ignored");
            return ResponseEntity.status(200).body("ignore");
        }
        userService.updateUserName(name, email);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "/activate/{id}/{code}/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultAnswer> activateUser(
            @PathVariable String id, @PathVariable String code, @PathVariable String email) throws BadRequestException {
        return ResponseEntity.status(200).body(userService.activateUserById(id, code, email));
    }

    @PatchMapping(path = "/recover/{id}/password")
    public ResponseEntity<DefaultAnswer> recoverPassword(@PathVariable String id) {
        return ResponseEntity.status(200).body(userService.recoverPassword(id));
    }

    @PatchMapping(path = "/request/{id}/{email}/delete")
    public ResponseEntity<DefaultAnswer> requestDelete(@PathVariable String id, @PathVariable String email) {
        return ResponseEntity.status(200).body(userService.requestDelete(id, email));
    }

    @DeleteMapping(path = "/delete/{id}/{token}")
    public ResponseEntity<DefaultAnswer> requestDoDelete(@PathVariable String id, @PathVariable String token) {
        return ResponseEntity.status(200).body(userService.doRequestDelete(id, token));
    }

    @PatchMapping(path = "/request/setPassword")
    public ResponseEntity<DefaultAnswer> setPassword(@Valid @RequestBody NewPasswordRequest request) {
        return ResponseEntity.status(200).body(userService.setPassword(request.id(), request.password(), request.token()));
    }
}
