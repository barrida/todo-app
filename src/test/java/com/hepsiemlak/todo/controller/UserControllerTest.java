package com.hepsiemlak.todo.controller;

import com.hepsiemlak.todo.exception.ErrorCode;
import com.hepsiemlak.todo.exception.UserExistsException;
import com.hepsiemlak.todo.model.User;
import com.hepsiemlak.todo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author suleyman.yildirim
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId("1")
                .username("newuser")
                .email("newuser@example.com")
                .build();
    }

    @Test
    void testRegisterUser_Success() throws Exception {

        when(userService.registerUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/v1/users")
                        .with(jwt().jwt((jwt) -> jwt.claim("scope", "message:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"1\", \"username\": \"newuser\", \"email\": \"newuser@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"));
    }

    @Test
    void testRegisterUser_UserExistsException() throws Exception {
        when(userService.registerUser(any(User.class))).thenThrow(new UserExistsException(ErrorCode.USER_EXISTS));

        mockMvc.perform(post("/v1/users")
                        .with(jwt().jwt((jwt) -> jwt.claim("scope", "message:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"1\", \"username\": \"existinguser\", \"email\": \"existinguser@example.com\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void testFindUserByUsername_Success() throws Exception {

       var existingUser =  User.builder()
                .userId("1")
                .username("existinguser")
                .email("existinguser@example.com")
                .build();

        when(userService.findUserByUsername("existinguser")).thenReturn(Optional.of(existingUser));

        mockMvc.perform(get("/v1/user")
                        .with(jwt().jwt((jwt) -> jwt.claim("scope", "message:read")))
                        .param("username", "existinguser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("existinguser"))
                .andExpect(jsonPath("$.email").value("existinguser@example.com"));
    }

    @Test
    void testFindUserByUsername_UserNotFoundException() throws Exception {
        when(userService.findUserByUsername("nonexistentuser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/user/nonexistentuser")
                        .with(jwt().jwt((jwt) -> jwt.claim("scope", "message:read"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindUserById_Success() throws Exception {

        var existingUser =  User.builder()
                .userId("1")
                .username("existinguser")
                .email("existinguser@example.com")
                .build();

        when(userService.findByUserId(anyString())).thenReturn(Optional.of(existingUser));

        mockMvc.perform(get("/v1/user/id")
                        .with(jwt().jwt((jwt) -> jwt.claim("scope", "message:read")))
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("existinguser"))
                .andExpect(jsonPath("$.email").value("existinguser@example.com"));
    }

    @Test
    void testFindUserById_UserNotFoundException() throws Exception {
        when(userService.findByUserId(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/user/1").with(jwt().jwt((jwt) -> jwt.claim("scope", "message:read"))))
                .andExpect(status().isNotFound());
    }

}