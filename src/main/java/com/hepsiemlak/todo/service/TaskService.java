package com.hepsiemlak.todo.service;

import com.hepsiemlak.todo.exception.ErrorCode;
import com.hepsiemlak.todo.exception.TaskNotFoundException;
import com.hepsiemlak.todo.exception.UserNotFoundException;
import com.hepsiemlak.todo.model.Task;
import com.hepsiemlak.todo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suleyman.yildirim
 */
@Service
public class TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getTasksByUser(Long userId) {
        return taskRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    public Task getTaskByIdAndUser(Long id, Long userId) {
        return taskRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TaskNotFoundException(id, userId));
    }

    public Task updateTaskForUser(Long id, Long userId, Task updatedTask) {
        Task existingTask = taskRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() ->  new TaskNotFoundException(id, userId));

        Task updatedExistingTask = Task.builder()
                .id(existingTask.getId())
                .userId(existingTask.getUserId())
                .title(updatedTask.getTitle())
                .description(updatedTask.getDescription())
                .dueDate(updatedTask.getDueDate())
                .priority(updatedTask.getPriority())
                .completed(updatedTask.getCompleted())
                .build();

        return taskRepository.save(updatedExistingTask);
    }
}
