package com.atharva.makersharks.controller;

import com.atharva.makersharks.model.User;
import com.atharva.makersharks.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@Slf4j
@Validated
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/fetch/self")
    public ResponseEntity<User> getSelfProfileHandler(@RequestHeader("Authorization") String token) {
        log.info("Fetching user profile for token: {}", token);

        try {
            User user = userService.findUserProfile(token);
            log.info("User profile retrieved for user ID: {}", user.getId());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            log.error("User not found for token: {}", token);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/fetch")
    public ResponseEntity<User> getUserProfileHandler(@RequestParam String username) {
        log.info("Fetching user profile for username: {}", username);

        try {
            User user = userService.findByUsername(username).get();
            log.info("User profile retrieved for user ID: {}", user.getId());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            log.error("User not found for username: {}", username);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
