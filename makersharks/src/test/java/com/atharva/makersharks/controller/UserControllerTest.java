package com.atharva.makersharks.controller;

import com.atharva.makersharks.exception.UserException;
import com.atharva.makersharks.model.User;
import com.atharva.makersharks.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetSelfProfileHandler_success() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        when(userService.findUserProfile(anyString())).thenReturn(user);

        mockMvc.perform(get("/api/users/fetch/self")
                        .header("Authorization", "Bearer dummyToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    void testGetSelfProfileHandler_userNotFound() throws Exception {
        when(userService.findUserProfile(anyString())).thenThrow(new UserException("User Not Found"));

        mockMvc.perform(get("/api/users/fetch/self")
                        .header("Authorization", "Bearer dummyToken"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserProfileHandler_success() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        when(userService.findByUsername(anyString())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/fetch")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    void testGetUserProfileHandler_userNotFound() throws Exception {
        when(userService.findByUsername(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/fetch")
                        .param("username", "testuser"))
                .andExpect(status().isNotFound());
    }
}
