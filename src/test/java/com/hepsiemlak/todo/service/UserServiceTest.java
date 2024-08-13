package com.hepsiemlak.todo.service;

import com.hepsiemlak.todo.exception.ErrorCode;
import com.hepsiemlak.todo.exception.UserExistsException;
import com.hepsiemlak.todo.exception.UserNotFoundException;
import com.hepsiemlak.todo.model.User;
import com.hepsiemlak.todo.repository.TaskRepository;
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

    @Mock
    private TaskRepository taskRepository;

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
        var result = userService.findUserByUsername(username);

        // Assert
        assertEquals(user, result);
        Mockito.verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindUserByUsername_UserNotFoundException() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.findUserByUsername("nonexistentuser");
        });

        // Assert
        assert(exception.getErrorCode()).equals(ErrorCode.USER_NOT_FOUND);
        assert(exception.getMessage()).equals("User with username nonexistentuser not found.");
    }

    @Test
    void testFindByUserId_UserNotFoundException() {
        // Arrange
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.findByUserId("nonexistentId");
        });

        // Assert
        assert(exception.getErrorCode()).equals(ErrorCode.USER_NOT_FOUND);
        assert(exception.getMessage()).equals("User with ID nonexistentId not found.");
    }
}