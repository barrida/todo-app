package com.hepsiemlak.todo.controller;

import com.hepsiemlak.todo.exception.ErrorCode;
import com.hepsiemlak.todo.exception.TaskNotFoundException;
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
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    @Operation(summary = "Retrieve all tasks for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of tasks",
                    content = @Content(schema = @Schema(implementation = Task.class)))
    })
    @GetMapping("/tasks")
    @PreAuthorize("hasAuthority('SCOPE_message:read')")
    public ResponseEntity<List<Task>> getAllTasksByUser(@RequestParam("userId") @NotNull(message = "User ID is required") Long userId) {
        List<Task> tasks = taskService.getTasksByUser(userId);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Retrieve a task by ID for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found",
                    content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = TaskNotFoundException.class)))
    })
    @GetMapping("/users/{userId}/tasks/{taskId}")
    @PreAuthorize("hasAuthority('SCOPE_message:read')")
    public ResponseEntity<Task> getTaskByIdAndUser(@PathVariable Long taskId, @PathVariable Long userId) {
        Task task = taskService.getTaskByIdAndUser(taskId, userId);
        return ResponseEntity.ok(task);
    }


    @Operation(summary = "Update an existing task for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully",
                    content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "400", description = "Invalid task data provided"),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = TaskNotFoundException.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = UserNotFoundException.class))
            )
    })
    @PutMapping("/tasks/{id}")
    @PreAuthorize("hasAuthority('SCOPE_message:write')")
    public ResponseEntity<Task> updateTask(
            @PathVariable("id") @NotNull(message = "Task ID is required") Long id,
            @RequestParam("userId") @NotNull(message = "User ID is required") Long userId,
            @RequestBody @Validated Task updatedTask) {
        Task task = taskService.updateTaskForUser(id, userId, updatedTask);
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Delete an existing task for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = TaskNotFoundException.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = UserNotFoundException.class))
            )
    })
    @DeleteMapping("/tasks/{id}")
    @PreAuthorize("hasAuthority('SCOPE_message:write')")
    public ResponseEntity<Void> deleteTask(
            @PathVariable("id") @NotNull(message = "Task ID is required") Long id,
            @RequestParam("userId") @NotNull(message = "User ID is required") Long userId) {
        taskService.deleteTaskForUser(id, userId);
        return ResponseEntity.noContent().build();
    }

}
