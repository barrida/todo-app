package com.hepsiemlak.todo.service;

import com.hepsiemlak.todo.exception.UserExistsException;
import com.hepsiemlak.todo.model.User;
import com.hepsiemlak.todo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author suleyman.yildirim
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testRegisterUser_UserAlreadyExistsByUsername_ThrowsException() {
        // Arrange
        User user = new User();
        user.setUsername("existingUser");
        user.setEmail("newemail@example.com");

        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserExistsException.class, () -> userService.registerUser(user));
        Mockito.verify(userRepository, never()).save(user);
    }

    @Test
    void testRegisterUser_UserAlreadyExistsByEmail_ThrowsException() {
        // Arrange
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("existingemail@example.com");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(UserExistsException.class, () -> userService.registerUser(user));
        Mockito.verify(userRepository, never()).save(user);
    }

    @Test
    void testRegisterUser_UserDoesNotExist_SavesUser() {
        // Arrange
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("newemail@example.com");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User savedUser = userService.registerUser(user);

        // Assert
        assertEquals(user, savedUser);
        Mockito.verify(userRepository, times(1)).save(user);
    }

    @Test
    void testFindUserByUsername_UserExists_ReturnsUser() {
        // Arrange
        String username = "existingUser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findUserByUsername(username);

        // Assert
        assertEquals(Optional.of(user), result);
        Mockito.verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindUserByUsername_UserDoesNotExist_ReturnsEmptyOptional() {
        // Arrange
        String username = "nonExistingUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findUserByUsername(username);

        // Assert
        assertEquals(Optional.empty(), result);
        Mockito.verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindByUserId_UserExists_ReturnsUser() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByUserId(userId);

        // Assert
        assertEquals(Optional.of(user), result);
        Mockito.verify(userRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testFindByUserId_UserDoesNotExist_ReturnsEmptyOptional() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByUserId(userId);

        // Assert
        assertEquals(Optional.empty(), result);
        Mockito.verify(userRepository, times(1)).findByUserId(userId);
    }
}