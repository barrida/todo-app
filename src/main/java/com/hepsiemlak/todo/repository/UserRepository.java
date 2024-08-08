package com.hepsiemlak.todo.repository;

/**
 * @author suleyman.yildirim
 */

import com.hepsiemlak.todo.model.User;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CouchbaseRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUserId(Long id);
    Optional<User> findByEmail(String email);
}
