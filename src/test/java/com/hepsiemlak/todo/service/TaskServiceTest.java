package com.hepsiemlak.todo.service;

import com.hepsiemlak.todo.exception.ErrorCode;
import com.hepsiemlak.todo.exception.TaskNotFoundException;
import com.hepsiemlak.todo.exception.UserNotFoundException;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.hepsiemlak.todo.contants.TodoTestConstants.TASK_ID;
import static com.hepsiemlak.todo.contants.TodoTestConstants.USER_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
                .taskId(TASK_ID)
                .title("title")
                .description("description")
                .dueDate("2024-08-30")
                .priority("High")
                .completed(false)
                .userId(USER_ID)
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
                .taskId(TASK_ID)
                .title("title")
                .description("description")
                .dueDate("2024-08-30")
                .priority("High")
                .completed(false)
                .userId(USER_ID)
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
        String  taskId = task.getTaskId();
        String userId = task.getUserId();
        Mockito.when(taskRepository.findByTaskIdAndUserId(taskId, userId)).thenReturn(Optional.of(task));

        // Act
        Task foundTask = taskService.getTaskByIdAndUser(taskId, userId);

        // Assert
        assertNotNull(foundTask);
        assertEquals(taskId, foundTask.getTaskId());
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
        String taskId = "1";
        String userId = "123";
        Mockito.when(taskRepository.findByTaskIdAndUserId(taskId, userId)).thenReturn(Optional.empty());

        // Act
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTaskByIdAndUser(taskId, userId);
        });

        // Assert
        assertEquals(ErrorCode.TASK_NOT_FOUND, exception.getErrorCode());
        assertEquals("Task with ID 1 not found for user 123", exception.getMessage());
    }

    @Test
    void getTasksByUser_ShouldReturnTasks_WhenUserExists() {
        // Arrange
        List<Task> expectedTasks = Arrays.asList(
                new Task("1", "Task 1", "Description 1", "2024-08-15", "High", false, "1"),
                new Task("2", "Task 2", "Description 2", "2024-08-16", "Medium", true, "1")
        );

        when(taskRepository.findByUserId(USER_ID)).thenReturn(Optional.of(expectedTasks));

        // Act
        List<Task> actualTasks = taskService.getTasksByUser(USER_ID);

        // Assert
        assertNotNull(actualTasks);
        assertEquals(expectedTasks.size(), actualTasks.size());
        assertEquals(expectedTasks, actualTasks);
        verify(taskRepository, times(1)).findByUserId(USER_ID);
    }

    @Test
    void getTasksByUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        when(taskRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            taskService.getTasksByUser(USER_ID);
        });

        assertEquals("User with ID %s was not found or has no tasks.".formatted(USER_ID), exception.getMessage());
        verify(taskRepository, times(1)).findByUserId(USER_ID);
    }

    @Test
    void deleteTaskForUser_ShouldDeleteTask_WhenTaskExists() {
        // Arrange
        Task existingTask = new Task(TASK_ID, "Sample Task", "Sample Description", "2024-09-01", "High", false, USER_ID);

        when(taskRepository.findByTaskIdAndUserId(TASK_ID, USER_ID)).thenReturn(Optional.of(existingTask));

        // Act
        taskService.deleteTaskForUser(TASK_ID, USER_ID);

        // Assert
        verify(taskRepository, times(1)).delete(existingTask);
    }

    @Test
    void deleteTaskForUser_ShouldThrowTaskNotFoundException_WhenTaskDoesNotExist() {
        // Arrange

        when(taskRepository.findByTaskIdAndUserId(TASK_ID, USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.deleteTaskForUser(TASK_ID, USER_ID);
        });

        verify(taskRepository, times(0)).delete(any(Task.class));
    }
}