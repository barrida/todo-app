package com.hepsiemlak.todo.exception;

import lombok.Getter;

/**
 * @author suleyman.yildirim
 */
@Getter
public enum ErrorCode {

    USER_NOT_FOUND("USER_NOT_FOUND"),
    USER_EXISTS("USER_EXISTS"),
    TASK_NOT_FOUND("TASK_NOT_FOUND");

    private final String code;

    ErrorCode(String code) {
        this.code = code;

    }

}

