package com.hepsiemlak.todo.controller;

import com.hepsiemlak.todo.exception.ErrorCode;
import com.hepsiemlak.todo.exception.UserExistsException;
import com.hepsiemlak.todo.exception.UserNotFoundException;
import com.hepsiemlak.todo.model.User;
import com.hepsiemlak.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author suleyman.yildirim
 */
@RestController
@RequestMapping("/v1")
@Tag(name = "User", description = "User management APIs")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "409", description = "Username or email already exists",
                    content = @Content(schema = @Schema(implementation = UserExistsException.class)))
    })
    @PostMapping("register-user")
    @PreAuthorize("hasAuthority('SCOPE_message:write')")
    public ResponseEntity<User> registerUser(@RequestBody @Valid User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.status(201).body(registeredUser);
        } catch (UserExistsException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @Operation(summary = "Find a user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = UserNotFoundException.class)))
    })
    @GetMapping("/user")
    @PreAuthorize("hasAuthority('SCOPE_message:read')")
    public ResponseEntity<User> findUserByUsername(@RequestParam String username) {
        Optional<User> user = userService.findUserByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    @Operation(summary = "Find a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = UserNotFoundException.class)))
    })
    @GetMapping("/user/id")
    @PreAuthorize("hasAuthority('SCOPE_message:read')")
    public ResponseEntity<User> findUserById(@RequestParam Long id) {
        Optional<User> user = userService.findByUserId(id);
        return user.map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
    }
}
