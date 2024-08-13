package com.hepsiemlak.todo.service;


import com.couchbase.client.core.error.CouchbaseException;
import com.hepsiemlak.todo.exception.ErrorCode;
import com.hepsiemlak.todo.exception.UserExistsException;
import com.hepsiemlak.todo.exception.UserNotFoundException;
import com.hepsiemlak.todo.model.User;
import com.hepsiemlak.todo.repository.TaskRepository;
import com.hepsiemlak.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public User registerUser(User user) {
        try {
            Optional<User> existingUserId = userRepository.findByUserId(user.getUserId());

            if (existingUserId.isPresent()) {
                throw new UserExistsException(ErrorCode.USER_EXISTS,
                        "User with ID %s already exists.".formatted(user.getUserId()));
            }
            return userRepository.save(user);
        } catch (CouchbaseException e) {
            throw new CouchbaseException("Failed to save user  with ID %s to Couchbase".formatted(user.getUserId()), e);
        }
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::loadTasksForUser)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND,
                        "User with username %s not found.".formatted(username)));
    }

    public User findByUserId(String id) {
        return userRepository.findByUserId(id)
                .map(this::loadTasksForUser)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND,
                        "User with ID %s not found.".formatted(id)));
    }

    private User loadTasksForUser(User user) {
        try {
            taskRepository.findByUserId(user.getUserId()).ifPresent(user::setTasks);
            return user;
        } catch (CouchbaseException e) {
            throw new CouchbaseException("Failed to load tasks for user with ID %s.".formatted(user.getUserId()), e);
        }
    }
}
