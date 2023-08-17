package com.example.todoserver.model;

import com.example.todoserver.annotation.BatchUpdatable;
import com.example.todoserver.annotation.NullOrNotBlank;
import com.example.todoserver.configuration.ConstantValues;
import com.example.todoserver.web.controller.validation.group.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor(force = true)
@Getter
@Setter
@Accessors(chain = true)
public class User {

    @JsonProperty(value = ConstantValues.User.Json.USERNAME)
    @NotBlank(message = ConstantValues.User.Error.USERNAME_NEEDED, groups = {UserValidationGroup.Create.class})
    @Length(
            min = ConstantValues.User.USERNAME_MIN_LENGTH,
            max = ConstantValues.User.USERNAME_MAX_LENGTH,
            message = ConstantValues.User.Error.USERNAME_INVALID_LENGTH,
            groups = {UserValidationGroup.Create.class}
    )
    @Id
    @Column(name = "username", length = ConstantValues.User.USERNAME_MAX_LENGTH)
    private String username;

    @JsonProperty(value = ConstantValues.User.Json.PASSWORD, access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = ConstantValues.User.Error.PASSWORD_NEEDED, groups = {UserValidationGroup.Create.class, UserValidationGroup.UpdatePassword.class})
    @Length(
            min = ConstantValues.User.PASSWORD_MIN_LENGTH,
            max = ConstantValues.User.PASSWORD_MAX_LENGTH,
            message = ConstantValues.User.Error.PASSWORD_INVALID_LENGTH,
            groups = {UserValidationGroup.Create.class, UserValidationGroup.UpdatePassword.class}
    )
    @Column(name = "password", length = 60, nullable = false) // Here the length of the password is determined by the Bcrypt hashed length
    private String password;

    @JsonProperty(value = ConstantValues.User.Json.EMAIL)
    @NullOrNotBlank(message = ConstantValues.User.Error.EMAIL_NEEDED, groups = {UserValidationGroup.Create.class})
    @NotBlank(message = ConstantValues.User.Error.EMAIL_NEEDED, groups = {UserValidationGroup.UpdateEmail.class})
    @Email(message = ConstantValues.User.Error.EMAIL_IS_INVALID, groups = {UserValidationGroup.Create.class, UserValidationGroup.UpdateEmail.class})
    @Length(
            max = ConstantValues.User.EMAIL_MAX_LENGTH,
            message = ConstantValues.User.Error.EMAIL_IS_INVALID,
            groups = {UserValidationGroup.Create.class, UserValidationGroup.UpdateEmail.class}
    )
    @Column(name = "email", length = ConstantValues.User.EMAIL_MAX_LENGTH)
    private String email;

    @JsonProperty(value = ConstantValues.User.Json.CREATED_AT, access = JsonProperty.Access.READ_ONLY)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @JsonProperty(value = ConstantValues.User.Json.FORENAME)
    @NotBlank(message = ConstantValues.User.Error.FORENAME_NEEDED, groups = {UserValidationGroup.Create.class})
    @NullOrNotBlank(message = ConstantValues.User.Error.FORENAME_NEEDED, groups = {UserValidationGroup.Update.class})
    @Length(
            min = ConstantValues.User.FORENAME_MIN_LENGTH,
            max = ConstantValues.User.FORENAME_MAX_LENGTH,
            message = ConstantValues.User.Error.FORENAME_IS_INVALID,
            groups = {UserValidationGroup.Create.class, UserValidationGroup.Update.class}
    )
    @BatchUpdatable
    @Column(name = "forename", length = ConstantValues.User.FORENAME_MAX_LENGTH, nullable = false)
    private String forename;

    @JsonProperty(value = ConstantValues.User.Json.SURNAME)
    @NotBlank(message = ConstantValues.User.Error.SURNAME_NEEDED, groups = {UserValidationGroup.Create.class})
    @NullOrNotBlank(message = ConstantValues.User.Error.SURNAME_NEEDED, groups = {UserValidationGroup.Update.class})
    @Length(
            min = ConstantValues.User.SURNAME_MIN_LENGTH,
            max = ConstantValues.User.SURNAME_MAX_LENGTH,
            message = ConstantValues.User.Error.SURNAME_IS_INVALID,
            groups = {UserValidationGroup.Create.class, UserValidationGroup.Update.class}
    )
    @BatchUpdatable
    @Column(name = "surname", length = ConstantValues.User.SURNAME_MAX_LENGTH, nullable = false)
    private String surname;

    @JsonIgnore
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "username"))
    @Column(name = "role")
    private List<String> roles;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private List<Todo> todos;
}
