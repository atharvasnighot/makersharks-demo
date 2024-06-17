package com.atharva.makersharks.controller;

import com.atharva.makersharks.config.TokenProvider;
import com.atharva.makersharks.model.User;
import com.atharva.makersharks.repository.UserRepository;
import com.atharva.makersharks.request.LoginRequest;
import com.atharva.makersharks.response.AuthResponse;
import com.atharva.makersharks.service.CustomUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CustomUserService customUserService;

    @PostMapping(value = "/register", produces = "application/json")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {
        String email = user.getEmail();
        String username = user.getUsername();
        String password = user.getPassword();

        log.info("Signup request received for email: {}", email);

        User isUser = userRepository.findByEmail(email);
        if (isUser != null) {
            log.warn("Email Id {} already registered", email);
            throw new Exception("Email Id already registered");
        }

        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setUsername(username);
        createdUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(createdUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        AuthResponse response = AuthResponse.builder()
                .jwt(jwt)
                .isAuth(true)
                .build();

        log.info("User {} registered successfully", email);
        log.debug("Generated JWT: {}", jwt);

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginHandler(@RequestBody LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        log.info("Sign in request received for email: {}", email);

        Authentication authentication = authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        AuthResponse response = new AuthResponse(jwt, true);

        log.info("User {} signed in successfully", email);
        log.debug("Generated JWT: {}", jwt);

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    public Authentication authenticate(String username, String password) {
        log.info("Authenticating user: {}", username);

        UserDetails userDetails = customUserService.loadUserByUsername(username);

        if (userDetails == null) {
            log.warn("Invalid Username: {}", username);
            throw new BadCredentialsException("Invalid Username or Password");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            log.warn("Invalid Password for user: {}", username);
            throw new BadCredentialsException("Invalid Password or Username");
        }

        log.info("User {} authenticated successfully", username);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
