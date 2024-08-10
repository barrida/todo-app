package com.hepsiemlak.todo.exception;

/**
 * @author suleyman.yildirim
 */
public class UserNotFoundException extends BaseException{
    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
