package com.example.todo.service.validation.implementation;

import com.example.todo.configuration.ConstantValues;
import com.example.todo.model.PasswordUpdateRequest;
import com.example.todo.model.validation.ValidationGroup;
import com.example.todo.service.validation.PasswordUpdateRequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class PasswordUpdateRequestValidatorImpl extends AbstractModelValidator<PasswordUpdateRequest> implements PasswordUpdateRequestValidator {

    private final Logger logger = LoggerFactory.getLogger(PasswordUpdateRequestValidatorImpl.class);

    @Override
    public void validate(PasswordUpdateRequest updateRequest) {

        logger.debug("Parameters:: updateRequest: {}", updateRequest);

        Assert.notNull(updateRequest, ConstantValues.RestApi.Error.REQUEST_BODY_NEEDED);
        logger.debug("Not null check passed for updateRequest object");

        handleConstraintViolation(updateRequest, ValidationGroup.User.UpdatePassword.class);

        logger.info("Validation completed for PasswordUpdateRequest object");
    }
}
