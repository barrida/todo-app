package com.hepsiemlak.todo.service;


import com.hepsiemlak.todo.exception.ErrorCode;
import com.hepsiemlak.todo.exception.UserExistsException;
import com.hepsiemlak.todo.model.User;
import com.hepsiemlak.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User registerUser(User user) {
        Optional<User> existingUserId = userRepository.findByUserId(user.getUserId());

        if (existingUserId.isPresent()) {
            throw new UserExistsException(ErrorCode.USER_EXISTS);
        }
        return userRepository.save(user);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByUserId(String id) {
        return userRepository.findByUserId(id);
    }
}
