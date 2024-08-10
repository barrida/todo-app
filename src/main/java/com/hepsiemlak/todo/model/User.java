package com.hepsiemlak.todo.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.util.List;

/**
 * @author suleyman.yildirim
 */
@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    @NotNull(message = "Id is required")
    private Long userId;

    @Field
    @NotBlank(message = "Username is required")
    private String username;

    @Field
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Field
    private List<Task> tasks;
}
