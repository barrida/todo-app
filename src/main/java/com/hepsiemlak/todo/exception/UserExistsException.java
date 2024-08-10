package com.hepsiemlak.todo.exception;

/**
 * @author suleyman.yildirim
 */
public class UserExistsException extends BaseException{
    public UserExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
