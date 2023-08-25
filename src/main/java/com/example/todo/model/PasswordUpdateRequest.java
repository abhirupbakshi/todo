package com.example.todo.model;

import com.example.todo.configuration.ConstantValues;
import com.example.todo.model.validation.ValidationGroup;
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
    @NotNull(message = ConstantValues.PasswordUpdateRequest.Error.CURRENT_NEEDED, groups = {ValidationGroup.User.UpdatePassword.class})
    @Valid
    User current;

    @JsonProperty(ConstantValues.PasswordUpdateRequest.Json.MODIFIED)
    @NotNull(message = ConstantValues.PasswordUpdateRequest.Error.MODIFIED_NEEDED, groups = {ValidationGroup.User.UpdatePassword.class})
    @Valid
    User modified;
}
