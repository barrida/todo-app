package com.hepsiemlak.todo.repository;

import com.hepsiemlak.todo.model.Task;
import org.springframework.data.couchbase.repository.CouchbaseRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author suleyman.yildirim
 */
public interface TaskRepository extends CouchbaseRepository<Task, String> {
    Optional<List<Task>> findByUserId(Long id);
    Optional<Task> findByIdAndUserId(Long id, String userId);
}