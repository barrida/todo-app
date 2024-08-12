package com.hepsiemlak.todo.service;

import com.hepsiemlak.todo.exception.UserExistsException;
import com.hepsiemlak.todo.model.User;
import com.hepsiemlak.todo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.hepsiemlak.todo.contants.TodoTestConstants.USER_ID;
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

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId(USER_ID)
                .username("newuser")
                .email("newuser@example.com")
                .build();
    }

    @Test
    void testRegisterUser_UserAlreadyExistsByUserId_ThrowsException() {
        // Arrange
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(UserExistsException.class, () -> userService.registerUser(user));
        Mockito.verify(userRepository, never()).save(user);
    }

    @Test
    void testRegisterUser_UserDoesNotExist_SavesUser() {
        // Arrange
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());
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

        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByUserId(USER_ID);

        // Assert
        assertEquals(Optional.of(user), result);
        Mockito.verify(userRepository, times(1)).findByUserId(USER_ID);
    }

    @Test
    void testFindByUserId_UserDoesNotExist_ReturnsEmptyOptional() {
        // Arrange

        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByUserId(USER_ID);

        // Assert
        assertEquals(Optional.empty(), result);
        Mockito.verify(userRepository, times(1)).findByUserId(USER_ID);
    }
}