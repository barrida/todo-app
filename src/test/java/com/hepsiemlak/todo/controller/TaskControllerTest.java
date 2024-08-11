package com.hepsiemlak.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hepsiemlak.todo.config.OAuth2ResourceServerSecurityConfiguration;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
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
}