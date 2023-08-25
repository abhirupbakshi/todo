package com.example.todo.service.validation.implementation;

import com.example.todo.configuration.ConstantValues;
import com.example.todo.model.User;
import com.example.todo.model.validation.ValidationGroup;
import com.example.todo.service.validation.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * An implementation of {@link UserValidator}
 */
@Component
public class UserValidatorImpl extends AbstractModelValidator<User> implements UserValidator {

    private final Logger logger = LoggerFactory.getLogger(UserValidatorImpl.class);

    @Override
    public void validate(User user, UserValidator.Operation operation) {

        logger.debug("Parameters:: user: {}, operation: {}", user, operation);

        Assert.notNull(user, ConstantValues.RestApi.Error.REQUEST_BODY_NEEDED);
        logger.debug("Not null check passed for User object");

        if (operation == null) throw new RuntimeException("Operation cannot be null");
        logger.debug("Not null check passed for operation");

        switch (operation) {
            case Create -> handleConstraintViolation(user, ValidationGroup.User.Create.class);
            case Update -> handleConstraintViolation(user, ValidationGroup.User.Update.class);
            case UpdateEmail -> handleConstraintViolation(user, ValidationGroup.User.UpdateEmail.class);
            case UpdatePassword -> handleConstraintViolation(user, ValidationGroup.User.UpdatePassword.class);
        }

        logger.info("Validation completed for user object");
    }
}
