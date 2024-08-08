package com.hepsiemlak.todo.exception;

import lombok.Getter;

/**
 * @author suleyman.yildirim
 */
@Getter
public enum ErrorCode {

    USER_NOT_FOUND("USER_NOT_FOUND", "User not found"),
    INVALID_INPUT("INVALID_INPUT", "Invalid input");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}

