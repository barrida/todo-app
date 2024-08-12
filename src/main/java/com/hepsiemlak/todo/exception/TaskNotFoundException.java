package com.hepsiemlak.todo.exception;

/**
 * @author suleyman.yildirim
 */

public class TaskNotFoundException extends BaseException{
    public TaskNotFoundException(String id, String userId) {
        super(ErrorCode.TASK_NOT_FOUND, "Task with ID " + id + " not found for user " + userId);
    }
}
