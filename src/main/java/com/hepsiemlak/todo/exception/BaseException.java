package com.hepsiemlak.todo.exception;

import lombok.Getter;

/**
 * @author suleyman.yildirim
 */
@Getter
public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;

    protected BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

