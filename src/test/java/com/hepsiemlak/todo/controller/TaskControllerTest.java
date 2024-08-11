package com.hepsiemlak.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hepsiemlak.todo.config.OAuth2ResourceServerSecurityConfiguration;
import com.hepsiemlak.todo.exception.ErrorCode;
import com.hepsiemlak.todo.exception.TaskNotFoundException;
import com.hepsiemlak.todo.exception.UserNotFoundException;
import com.hepsiemlak.todo.model.Task;
import com.hepsiemlak.todo.model.User;
import com.hepsiemlak.todo.service.TaskService;
import com.hepsiemlak.todo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author suleyman.yildirim
 */
@WebMvcTest(TaskController.class)
@Import(OAuth2ResourceServerSecurityConfiguration.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @MockBean
    private TaskService taskService;

    private Task task;
    private User user;

    @BeforeEach
    public void setup() {
        task = Task.builder()
                .id(1L)
                .title("title")
                .description("description")
                .dueDate("2024-08-30")
                .priority("High")
                .completed(false)
                .userId(1L)
                .build();

        user = User.builder()
                .userId(1L)
                .email("sy@example.com")
                .tasks(List.of(task))
                .build();
    }

    @Test
    void testCreateTask_Success() throws Exception {
        // Arrange
        Task createdTask = Task.builder()
                .id(1L)
                .title("title")
                .description("description")
                .dueDate("2024-08-30")
                .priority("High")
                .completed(false)
                .userId(1L)
                .build();

        when(userService.findByUserId(1L)).thenReturn(Optional.of(user));
        when(taskService.createTask(task)).thenReturn(createdTask);

        // Act & Assert
        mockMvc.perform(post("/v1/create-task")
                        .with(jwt().jwt((jwt) -> jwt.claim("scope", "message:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task))) // Convert Task object to JSON
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.dueDate").value("2024-08-30"))
                .andExpect(jsonPath("$.priority").value("High"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.userId").value("1"));
    }

    @Test
    void testCreateTask_UserNotFound() throws Exception {
        // Arrange
        when(userService.findByUserId(2L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/v1/create-task")
                        .with(jwt().jwt((jwt) -> jwt.claim("scope", "message:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isNotFound())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof UserNotFoundException));
    }

    @Test
    void testCreateTask_InvalidTaskFields() throws Exception {
        // Arrange task with invalid task title
        Task task = Task.builder()
                .build();

        // Act & Assert
        mockMvc.perform(post("/v1/create-task")
                        .with(jwt().jwt((jwt) -> jwt.claim("scope", "message:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.error").value("Invalid user input"))
                .andExpect(jsonPath("$.message").value(containsInAnyOrder(
                        "Id is required",
                        "Title is required",
                        "Description is required",
                        "Due date is required",
                        "Priority is required",
                        "User id is required",
                        "Status is required")));
    }

    @Test
    void testGetTaskByIdAndUser_Success() throws Exception {
        // Arrange
        when(taskService.getTaskByIdAndUser(1L, 1L)).thenReturn(task);

        // Act & Assert
        mockMvc.perform(get("/v1/users/1/tasks/1")
                        .with(jwt().jwt((jwt) -> jwt.claim("scope", "message:read")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value(task.getTitle()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.dueDate").value(task.getDueDate()))
                .andExpect(jsonPath("$.priority").value(task.getPriority()))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.userId").value(task.getUserId()));
    }

    @Test
    void testGetTaskByIdAndUser_TaskNotFound() throws Exception {
        // Arrange
        when(taskService.getTaskByIdAndUser(1L, 1L)).thenThrow(new TaskNotFoundException(1L, 1L));

        // Act & Assert
        mockMvc.perform(get("/v1/users/1/tasks/1")
                        .with(jwt().jwt((jwt) -> jwt.claim("scope", "message:read")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message:read")
    void getAllTasks_ShouldReturnTasks_WhenUserExists() throws Exception {
        // Arrange
        Long userId = 1L;
        List<Task> tasks = Arrays.asList(
                new Task(1L, "Task 1", "Description 1", "2024-08-15", "High", false, userId),
                new Task(2L, "Task 2", "Description 2", "2024-08-16", "Medium", true, userId)
        );

        when(taskService.getTasksByUser(userId)).thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/v1/tasks")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));

        verify(taskService, times(1)).getTasksByUser(userId);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message:read")
    void getAllTasks_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Arrange
        Long userId = 1L;

        when(taskService.getTasksByUser(userId)).thenThrow(new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        // Act & Assert
        mockMvc.perform(get("/v1/tasks")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).getTasksByUser(userId);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message:read")
    void getAllTasks_ShouldReturnBadRequest_WhenInvalidUserIdProvided() throws Exception {
        // Arrange
        String invalidUserId = "abc";  // Non-numeric userId

        // Act & Assert
        mockMvc.perform(get("/v1/tasks")
                        .param("userId", invalidUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message:read")
    void getAllTasks_ShouldReturnBadRequest_WhenUserIdIsMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}