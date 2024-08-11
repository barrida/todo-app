package com.hepsiemlak.todo.controller;

import com.hepsiemlak.todo.exception.ErrorCode;
import com.hepsiemlak.todo.exception.UserNotFoundException;
import com.hepsiemlak.todo.model.Task;
import com.hepsiemlak.todo.model.User;
import com.hepsiemlak.todo.service.TaskService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author suleyman.yildirim
 */
@RestController
@RequestMapping("/v1")
@Tag(name = "Task", description = "Task management APIs")
@RequiredArgsConstructor
@Validated
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @Operation(summary = "Create a new task for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully",
                    content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "User Not Found",
                    content = @Content(schema = @Schema(implementation = UserNotFoundException.class)))
    })
    @PostMapping("create-task")
    @PreAuthorize("hasAuthority('SCOPE_message:write')")
    public ResponseEntity<Task> createTask(@RequestBody @Valid Task task) {
        User user = userService.findByUserId(task.getUserId())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        task.setUserId(user.getUserId());
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.status(201).body(createdTask);
    }

}
