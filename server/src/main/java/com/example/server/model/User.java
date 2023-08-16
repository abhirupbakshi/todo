package com.example.server.model;

import com.example.server.annotation.BatchUpdatable;
import com.example.server.annotation.NullOrNotBlank;
import com.example.server.configuration.Constants;
import com.example.server.web.controller.validation.group.*;
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

    @JsonProperty(value = Constants.User.Json.USERNAME)
    @NotBlank(message = Constants.User.Error.USERNAME_NEEDED, groups = {UserValidationGroup.Create.class})
    @Length(
            min = Constants.User.USERNAME_MIN_LENGTH,
            max = Constants.User.USERNAME_MAX_LENGTH,
            message = Constants.User.Error.USERNAME_INVALID_LENGTH,
            groups = {UserValidationGroup.Create.class}
    )
    @Id
    @Column(name = "username", length = Constants.User.USERNAME_MAX_LENGTH)
    private String username;

    @JsonProperty(value = Constants.User.Json.PASSWORD, access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = Constants.User.Error.PASSWORD_NEEDED, groups = {UserValidationGroup.Create.class, UserValidationGroup.UpdatePassword.class})
    @Length(
            min = Constants.User.PASSWORD_MIN_LENGTH,
            max = Constants.User.PASSWORD_MAX_LENGTH,
            message = Constants.User.Error.PASSWORD_INVALID_LENGTH,
            groups = {UserValidationGroup.Create.class, UserValidationGroup.UpdatePassword.class}
    )
    @Column(name = "password", length = 60, nullable = false) // Here the length of the password is determined by the Bcrypt hashed length
    private String password;

    @JsonProperty(value = Constants.User.Json.EMAIL)
    @NullOrNotBlank(message = Constants.User.Error.EMAIL_NEEDED, groups = {UserValidationGroup.Create.class})
    @NotBlank(message = Constants.User.Error.EMAIL_NEEDED, groups = {UserValidationGroup.UpdateEmail.class})
    @Email(message = Constants.User.Error.EMAIL_IS_INVALID, groups = {UserValidationGroup.Create.class, UserValidationGroup.UpdateEmail.class})
    @Length(
            max = Constants.User.EMAIL_MAX_LENGTH,
            message = Constants.User.Error.EMAIL_IS_INVALID,
            groups = {UserValidationGroup.Create.class, UserValidationGroup.UpdateEmail.class}
    )
    @Column(name = "email", length = Constants.User.EMAIL_MAX_LENGTH)
    private String email;

    @JsonProperty(value = Constants.User.Json.CREATED_AT, access = JsonProperty.Access.READ_ONLY)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @JsonProperty(value = Constants.User.Json.FORENAME)
    @NotBlank(message = Constants.User.Error.FORENAME_NEEDED, groups = {UserValidationGroup.Create.class})
    @NullOrNotBlank(message = Constants.User.Error.FORENAME_NEEDED, groups = {UserValidationGroup.Update.class})
    @Length(
            min = Constants.User.FORENAME_MIN_LENGTH,
            max = Constants.User.FORENAME_MAX_LENGTH,
            message = Constants.User.Error.FORENAME_IS_INVALID,
            groups = {UserValidationGroup.Create.class, UserValidationGroup.Update.class}
    )
    @BatchUpdatable
    @Column(name = "forename", length = Constants.User.FORENAME_MAX_LENGTH, nullable = false)
    private String forename;

    @JsonProperty(value = Constants.User.Json.SURNAME)
    @NotBlank(message = Constants.User.Error.SURNAME_NEEDED, groups = {UserValidationGroup.Create.class})
    @NullOrNotBlank(message = Constants.User.Error.SURNAME_NEEDED, groups = {UserValidationGroup.Update.class})
    @Length(
            min = Constants.User.SURNAME_MIN_LENGTH,
            max = Constants.User.SURNAME_MAX_LENGTH,
            message = Constants.User.Error.SURNAME_IS_INVALID,
            groups = {UserValidationGroup.Create.class, UserValidationGroup.Update.class}
    )
    @BatchUpdatable
    @Column(name = "surname", length = Constants.User.SURNAME_MAX_LENGTH, nullable = false)
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
