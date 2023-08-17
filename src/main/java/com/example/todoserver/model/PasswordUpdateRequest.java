package com.example.todoserver.model;

import com.example.todoserver.configuration.ConstantValues;
import com.example.todoserver.web.controller.validation.group.UserValidationGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor(force = true)
@Getter
@Setter
@Accessors(chain = true)
public class PasswordUpdateRequest {

    @JsonProperty(ConstantValues.PasswordUpdateRequest.Json.CURRENT)
    @NotNull(message = ConstantValues.PasswordUpdateRequest.Error.CURRENT_NEEDED, groups = {UserValidationGroup.UpdatePassword.class})
    @Valid
    User current;

    @JsonProperty(ConstantValues.PasswordUpdateRequest.Json.MODIFIED)
    @NotNull(message = ConstantValues.PasswordUpdateRequest.Error.MODIFIED_NEEDED, groups = {UserValidationGroup.UpdatePassword.class})
    @Valid
    User modified;
}
