package com.example.todo.service.validation;

import com.example.todo.model.User;

/**
 * An interface for validation of {@link User} objects
 */
public interface UserValidator {

    /**
     * The different operations that can be performed on a {@link User}
     */
    enum Operation {
        Create,
        Update,
        UpdateEmail,
        UpdatePassword
    }

    /**
     * Validates a {@link User} object according to the {@link Operation} passed. Each operation has
     * predefined validation rules, implemented using java validation api.
     * @param user The user to be validated
     * @param operation The operation to be performed
     */
    void validate(User user, UserValidator.Operation operation);
}
