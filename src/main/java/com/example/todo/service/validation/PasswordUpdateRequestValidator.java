package com.example.todo.service.validation;

import com.example.todo.model.PasswordUpdateRequest;

public interface PasswordUpdateRequestValidator {

    void validate(PasswordUpdateRequest updateRequest);
}
