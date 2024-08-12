package com.hepsiemlak.todo.repository;

import com.hepsiemlak.todo.model.Task;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author suleyman.yildirim
 */
public interface TaskRepository extends CrudRepository<Task, String> {
    Optional<List<Task>> findByUserId(String id);
    Optional<Task> findByTaskIdAndUserId(String taskId, String userId);
}