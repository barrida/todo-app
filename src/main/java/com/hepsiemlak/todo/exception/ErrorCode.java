package com.hepsiemlak.todo.exception;

import lombok.Getter;

/**
 * @author suleyman.yildirim
 */
@Getter
public enum ErrorCode {

    USER_NOT_FOUND("USER_NOT_FOUND", "User not found"),
    USER_EXISTS("USER_EXISTS", "User already exists"),
    TASK_NOT_FOUND("TASK_NOT_FOUND", "Task not found for the given user");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}

