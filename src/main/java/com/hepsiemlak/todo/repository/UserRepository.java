package com.hepsiemlak.todo.repository;


import com.hepsiemlak.todo.model.User;
import org.springframework.data.couchbase.repository.CouchbaseRepository;

import java.util.Optional;

/**
 * @author suleyman.yildirim
 */
public interface UserRepository extends CouchbaseRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUserId(Long id);
    Optional<User> findByEmail(String email);
}
