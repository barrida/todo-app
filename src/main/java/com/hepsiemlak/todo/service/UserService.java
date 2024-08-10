package com.hepsiemlak.todo.service;


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
        Optional<User> existingUserByUsername = userRepository.findByUsername(user.getUsername());
        Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());

        if (existingUserByUsername.isPresent() || existingUserByEmail.isPresent()) {
            throw new RuntimeException("Username or email already exists");
        }
        return userRepository.save(user);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByUserId(Long id) {
        return userRepository.findByUserId(id);
    }
}
