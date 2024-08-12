package com.hepsiemlak.todo.model;

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

/**
 * @author suleyman.yildirim
 */
@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    @NotNull(message = "Id is required")
    private String taskId;

    @Field
    @NotBlank(message = "Title is required")
    private String title;

    @Field
    @NotBlank(message = "Description is required")
    private String description;

    @Field
    @NotBlank(message = "Due date is required")
    private String dueDate;

    @Field
    @NotBlank(message = "Priority is required")
    private String priority;

    @Field
    @NotNull(message = "Status is required")
    private Boolean completed;

    @Field
    @NotNull(message = "User id is required")
    private String userId;
}