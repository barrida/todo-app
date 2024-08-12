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

    public List<Task> getTasksByUser(String userId) {
        return taskRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    public Task getTaskByIdAndUser(String taskId, String userId) {
        return taskRepository.findByTaskIdAndUserId(taskId, userId)
                .orElseThrow(() -> new TaskNotFoundException(taskId, userId));
    }

    public Task updateTaskForUser(String taskId, Task updatedTask) {

        Task existingTask = taskRepository.findByTaskIdAndUserId(taskId, updatedTask.getUserId())
                .orElseThrow(() ->  new TaskNotFoundException(taskId, updatedTask.getUserId()));

        Task updatedExistingTask = Task.builder()
                .taskId(taskId)
                .userId(existingTask.getUserId())
                .title(updatedTask.getTitle())
                .description(updatedTask.getDescription())
                .dueDate(updatedTask.getDueDate())
                .priority(updatedTask.getPriority())
                .completed(updatedTask.getCompleted())
                .build();

        return taskRepository.save(updatedExistingTask);
    }

    public void deleteTaskForUser(String taskId, String userId) {
        Task existingTask = taskRepository.findByTaskIdAndUserId(taskId, userId)
                .orElseThrow(() -> new TaskNotFoundException(taskId, userId));

        taskRepository.delete(existingTask);
    }
}
