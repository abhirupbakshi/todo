package com.example.todo.exception;

import java.util.List;

public class ConstraintValidationException extends RuntimeException {

    private List<String> messages;

    public List<String> getMessages() {
        return messages;
    }

    public ConstraintValidationException() {
        super();
    }

    public ConstraintValidationException(List<String> messages) {
        super();
        this.messages = messages;
    }

    public ConstraintValidationException(List<String> messages, Throwable cause) {
        super(cause);
        this.messages = messages;
    }

    public ConstraintValidationException(Throwable cause) {
        super(cause);
    }
}
