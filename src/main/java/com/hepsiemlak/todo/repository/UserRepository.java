package com.hepsiemlak.todo.repository;


import com.hepsiemlak.todo.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * @author suleyman.yildirim
 */
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUserId(String userId);
}
