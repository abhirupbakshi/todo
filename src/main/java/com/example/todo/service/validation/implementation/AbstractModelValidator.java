package com.example.todo.service.validation.implementation;

import com.example.todo.exception.ConstraintValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * This class contains a set of validation methods that are common to all model validators.
 * @param <T> The type of the model
 */
abstract class AbstractModelValidator<T> {

    private final Logger logger = LoggerFactory.getLogger(AbstractModelValidator.class);
    private final Validator validator;

    {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
            logger.debug("Validator has been initialized");
        }
        catch (Exception e) {
            logger.error("Failed to initialize validator", e);
            throw e;
        }
    }

    /**
     * Takes an object and an operation enum class (from its child interfaces), and returns a list of validation errors.
     * Returns an empty list if there are no errors.
     * @param model The model to be validated.
     * @param operation The operation to be performed.
     * @return A {@link List} of validation errors. Can be empty if there are no errors.
     */
    protected List<String> validate(T model, Class<?> ...operation) {

        logger.debug("Parameters:: model: {}, operation: {}", model, Arrays.toString(operation));

        if (model == null) throw new RuntimeException("Model cannot be null");
        logger.debug("Not null check passed for model");

        if (operation == null) throw new RuntimeException("Operation cannot be null");
        logger.debug("Not null check passed for operation");

        Set<ConstraintViolation<T>> violations = validator.validate(model, operation);
        logger.debug("Constraint violations: {}", violations);

        List<String> messages = violations
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        logger.debug("Validation messages: {}", messages);

        return messages;
    }

    /**
     * This method validates a model for on specific operations (denoted by classes). If any validation errors are found,
     * {@link ConstraintValidationException} is thrown with the list of validation errors messages.
     * @param model The model to be validated.
     * @param operation The class of the operations.
     */
    protected void handleConstraintViolation(T model, Class<?> ...operation) {

        logger.debug("Parameters:: model: {}, operation: {}", model, Arrays.toString(operation));

        if (model == null) throw new RuntimeException("Model cannot be null");
        logger.debug("Not null check passed for model");

        if (operation == null) throw new RuntimeException("Operation cannot be null");
        logger.debug("Not null check passed for operation");

        List<String> messages = this.validate(model, operation);
        logger.debug("Validation messages: {}", messages);

        if (!messages.isEmpty()) {
            throw new ConstraintValidationException(messages);
        }
    }
}
