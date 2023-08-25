package com.example.todo.annotation.implementation;

import com.example.todo.annotation.NullOrNotBlank;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A validator class for {@link NullOrNotBlank} annotation.
 */
public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {

    private final Logger logger = LoggerFactory.getLogger(NullOrNotBlankValidator.class);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        logger.debug("Parameters:: value: {}, context: {}", value, context);

        boolean result = value == null || value.trim().length() > 0;
        logger.debug("Result: {}", result);

        return result;
    }
}
