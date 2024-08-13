package com.hepsiemlak.todo.exception;

/**
 * @author suleyman.yildirim
 */

public class TaskNotFoundException extends BaseException{
    public TaskNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
