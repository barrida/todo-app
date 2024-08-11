package com.hepsiemlak.todo.service;

import com.hepsiemlak.todo.exception.ErrorCode;
import com.hepsiemlak.todo.exception.TaskNotFoundException;
import com.hepsiemlak.todo.model.Task;
import com.hepsiemlak.todo.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * @author suleyman.yildirim
 */

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;


    @InjectMocks
    private TaskService taskService;

    private Task task;

    @BeforeEach
    public void setUp() {
        task = Task.builder()
                .id(1L)
                .title("title")
                .description("description")
                .dueDate("2024-08-30")
                .priority("High")
                .completed(false)
                .userId(1L)
                .build();
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
    }

    @Test
    void testCreateTask_Success() {

        // Arrange
        Task created = Task.builder()
                .id(1L)
                .title("title")
                .description("description")
                .dueDate("2024-08-30")
                .priority("High")
                .completed(false)
                .userId(1L)
                .build();

        Mockito.when(taskRepository.save(any(Task.class))).thenReturn(created);

        // Act
        var createdTask = taskService.createTask(task);

        // Assert
        assertTrue(Objects.deepEquals(created, createdTask));

    }

    @Test
    void testGetTaskByIdAndUser_Success() {
        // Arrange
        Long taskId = task.getId();
        Long userId = task.getUserId();
        Mockito.when(taskRepository.findByIdAndUserId(taskId, userId)).thenReturn(Optional.of(task));

        // Act
        Task foundTask = taskService.getTaskByIdAndUser(taskId, userId);

        // Assert
        assertNotNull(foundTask);
        assertEquals(taskId, foundTask.getId());
        assertEquals("title", foundTask.getTitle());
        assertEquals("description", foundTask.getDescription());
        assertEquals("2024-08-30", foundTask.getDueDate());
        assertEquals("High", foundTask.getPriority());
        assertFalse(foundTask.getCompleted());
        assertEquals(userId, foundTask.getUserId());
    }

    @Test
    void testGetTaskByIdAndUser_TaskNotFound() {
        // Arrange
        Long taskId = 1L;
        Long userId = 123L;
        Mockito.when(taskRepository.findByIdAndUserId(taskId, userId)).thenReturn(Optional.empty());

        // Act
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTaskByIdAndUser(taskId, userId);
        });

        // Assert
        assertEquals(ErrorCode.TASK_NOT_FOUND, exception.getErrorCode());
        assertEquals("Task with ID 1 not found for user 123", exception.getMessage());
    }

}