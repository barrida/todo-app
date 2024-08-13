package com.hepsiemlak.todo.exception;

/**
 * @author suleyman.yildirim
 */
public class UserNotFoundException extends BaseException{
    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public UserNotFoundException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
