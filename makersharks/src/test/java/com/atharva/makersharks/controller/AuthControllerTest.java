package com.atharva.makersharks.controller;

import com.atharva.makersharks.config.TokenProvider;
import com.atharva.makersharks.model.User;
import com.atharva.makersharks.repository.UserRepository;
import com.atharva.makersharks.request.LoginRequest;
import com.atharva.makersharks.service.CustomUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private CustomUserService customUserService;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUserHandlerEmailAlreadyRegistered() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        Exception exception = assertThrows(Exception.class, () -> {
            authController.createUserHandler(user);
        });

        assertEquals("Email Id already registered", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testLoginHandlerInvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        when(customUserService.loadUserByUsername(loginRequest.getEmail())).thenReturn(null);

        assertThrows(BadCredentialsException.class, () -> {
            authController.loginHandler(loginRequest);
        });

        verify(tokenProvider, never()).generateToken(any());
    }
}
