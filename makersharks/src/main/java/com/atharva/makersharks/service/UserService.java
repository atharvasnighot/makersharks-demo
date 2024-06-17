package com.atharva.makersharks.service;

import com.atharva.makersharks.config.TokenProvider;
import com.atharva.makersharks.exception.UserException;
import com.atharva.makersharks.model.User;
import com.atharva.makersharks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) throws UserException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UserException("User Not Found");
        }
        return user;
    }

    public User findUserProfile(String jwt) throws BadCredentialsException, UserException {
        String email = tokenProvider.getEmailFromToken(jwt);

        if (email == null) {
            throw new BadCredentialsException("Invalid Token");
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UserException("User Not Found");
        }

        return user;
    }
}


