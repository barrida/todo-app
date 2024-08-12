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

import static com.hepsiemlak.todo.contants.TodoTestConstants.TASK_ID;
import static com.hepsiemlak.todo.contants.TodoTestConstants.USER_ID;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                .taskId(TASK_ID)
                .title("title")
                .description("description")
                .dueDate("2024-08-30")
                .priority("High")
                .completed(false)
                .userId(USER_ID)
                .build();

        user = User.builder()
                .userId(USER_ID)
                .email("sy@example.com")
                .tasks(List.of(task))
                .build();
    }

    @Test
    void testCreateTask_Success() throws Exception {
        // Arrange

        when(userService.findByUserId(anyString())).thenReturn(Optional.of(user));
        when(taskService.createTask(any(Task.class))).thenReturn(task);

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
                        .with(jwt().jwt((jwt) -> jwt.claim("scope", "message:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task))) // Convert Task object to JSON
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskId").value("1"))
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
        when(userService.findByUserId(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/v1/tasks")
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
        mockMvc.perform(post("/v1/tasks")
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
        when(taskService.getTaskByIdAndUser(anyString(), anyString())).thenReturn(task);

        // Act & Assert
        mockMvc.perform(get("/v1/users/1/tasks/1")
                        .with(jwt().jwt((jwt) -> jwt.claim("scope", "message:read")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(1L))
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
        when(taskService.getTaskByIdAndUser(anyString(), anyString())).thenThrow(new TaskNotFoundException("1", "1"));

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
        List<Task> tasks = Arrays.asList(
                new Task("1", "Task 1", "Description 1", "2024-08-15", "High", false, USER_ID),
                new Task("2", "Task 2", "Description 2", "2024-08-16", "Medium", true, USER_ID)
        );

        when(taskService.getTasksByUser(USER_ID)).thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/v1/tasks")
                        .param("userId", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));

        verify(taskService, times(1)).getTasksByUser(USER_ID);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message:read")
    void getAllTasks_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Arrange
        when(taskService.getTasksByUser(anyString())).thenThrow(new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        // Act & Assert
        mockMvc.perform(get("/v1/tasks")
                        .param("userId", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).getTasksByUser(USER_ID);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message:read")
    void getAllTasks_ShouldReturnBadRequest_WhenUserIdIsMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message:write")
    void updateTask_ShouldReturnUpdatedTask_WhenValidTaskDataIsProvided() throws Exception {
        // Arrange
        Task updatedTask = Task.builder()
                .taskId(TASK_ID)
                .userId(USER_ID)
                .title("Updated Task")
                .description("Updated Description")
                .dueDate("2024-09-01")
                .priority("Low")
                .completed(true)
                .build();

        when(taskService.updateTaskForUser(anyString(), any(Task.class))).thenReturn(updatedTask);

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/{id}", TASK_ID)
                        .param("userId", USER_ID)
                        .with(csrf())  // Add CSRF token if CSRF protection is enabled
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskId\": 1, \"title\": \"Updated Task\", \"description\": \"Updated Description\", \"dueDate\": \"2024-09-01\", \"priority\": \"Low\", \"completed\": true, \"userId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(TASK_ID))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.dueDate").value("2024-09-01"))
                .andExpect(jsonPath("$.priority").value("Low"))
                .andExpect(jsonPath("$.completed").value(true));

        verify(taskService, times(1)).updateTaskForUser(TASK_ID, updatedTask);
    }


    @Test
    @WithMockUser(authorities = "SCOPE_message:write")
    void updateTask_ShouldReturnNotFound_WhenTaskOrUserDoesNotExist() throws Exception {
        // Arrange
        Task updatedTask = Task.builder()
                .taskId(TASK_ID)
                .userId(USER_ID)
                .title("Updated Task")
                .description("Updated Description")
                .dueDate("2024-09-01")
                .priority("Low")
                .completed(true)
                .build();

        when(taskService.updateTaskForUser(TASK_ID, updatedTask)).thenThrow(new TaskNotFoundException(TASK_ID, USER_ID));

        // Act & Assert
        mockMvc.perform(put("/v1/tasks/{id}", TASK_ID)
                        .param("userId", String.valueOf(USER_ID))
                        .with(csrf())  // Include CSRF token if CSRF protection is enabled
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskId\": 1, \"title\": \"Updated Task\", \"description\": \"Updated Description\", \"dueDate\": \"2024-09-01\", \"priority\": \"Low\", \"completed\": true, \"userId\": 1}"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).updateTaskForUser(TASK_ID,  updatedTask);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message:write")
    void deleteTask_ShouldReturnNoContent_WhenTaskIsDeleted() throws Exception {

        // Act & Assert
        mockMvc.perform(delete("/v1/tasks/{id}", TASK_ID)
                        .param("userId", String.valueOf(USER_ID))
                        .with(csrf())  // Include CSRF token if CSRF protection is enabled
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTaskForUser(TASK_ID, USER_ID);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message:write")
    void deleteTask_ShouldReturnNotFound_WhenTaskOrUserDoesNotExist() throws Exception {
        // Arrange

        doThrow(new TaskNotFoundException(TASK_ID, USER_ID))
                .when(taskService).deleteTaskForUser(TASK_ID, USER_ID);

        // Act & Assert
        mockMvc.perform(delete("/v1/tasks/{id}", TASK_ID)
                        .param("userId", String.valueOf(USER_ID))
                        .with(csrf())  // Include CSRF token if CSRF protection is enabled
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).deleteTaskForUser(TASK_ID, USER_ID);
    }

}