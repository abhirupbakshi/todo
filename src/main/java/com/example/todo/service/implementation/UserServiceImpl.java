package com.example.todo.service.implementation;

import com.example.todo.configuration.ConstantValues;
import com.example.todo.configuration.EnvironmentValues;
import com.example.todo.exception.AlreadyExistException;
import com.example.todo.exception.NotFoundException;
import com.example.todo.model.PasswordUpdateRequest;
import com.example.todo.model.Role;
import com.example.todo.model.User;
import com.example.todo.repository.RoleRepository;
import com.example.todo.repository.UserRepository;
import com.example.todo.service.UserService;
import com.example.todo.service.validation.PasswordUpdateRequestValidator;
import com.example.todo.service.validation.UserValidator;
import com.example.todo.service.validation.implementation.UserValidatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An implementation of {@link UserService}.
 */
@Service
public class UserServiceImpl extends AbstractModelServiceImpl<User> implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConstantValues.DATE_TIME_FORMAT_PATTERN);
    private EnvironmentValues environmentValues;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserValidator userValidator;
    private PasswordUpdateRequestValidator passwordUpdateRequestValidator;

    @Autowired
    public void setEnvironmentValues(EnvironmentValues environmentValues) {
        this.environmentValues = environmentValues;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setUserValidator(UserValidatorImpl userValidatorImpl) {
        this.userValidator = userValidatorImpl;
    }

    @Autowired
    public void setPasswordUpdateRequestValidator(PasswordUpdateRequestValidator passwordUpdateRequestValidator) {
        this.passwordUpdateRequestValidator = passwordUpdateRequestValidator;
    }

    /**
     * Finds a user with the given username and returns an {@link Optional} {@link User}. Also takes an optional boolean parameter
     * throwException that indicates if and when an exception should be thrown. If throwException is true and the user
     * exists, then an {@link AlreadyExistException} is thrown. If throwException is false and the user does not exist,
     * then a {@link NotFoundException} is thrown. If throwException is null, then no exception is thrown.
     * @param username The username
     * @param throwException Boolean, indicating if an exception should be thrown and when.
     * @return The Optional user
     */
    private Optional<User> findUser(String username, Boolean throwException) {

        Optional<User> user = userRepository.findById(username);

        if (throwException != null && throwException && user.isPresent()) // Throw exception if present
            throw new AlreadyExistException(ConstantValues.User.Error.USER_EXISTS);
        else if (throwException != null && !throwException && user.isEmpty()) // Throw exception if not present
            throw new NotFoundException(ConstantValues.User.Error.USER_NOT_FOUND);

        return user;
    }

    @Override
    public User findUser(String username) {

        logger.debug("Parameters:: username: {}", username);

        if (username == null) throw new RuntimeException("Username cannot be null");
        logger.debug("Not null check passed for username");

        User user = findUser(username, false).get();
        logger.info("User with username: {} has been found", user.getUsername());

        return user;
    }

    @Override
    public User createUser(User user) {

        logger.debug("Parameters:: user: {}", user);

        userValidator.validate(user, UserValidator.Operation.Create);
        logger.debug("Validation for user-creation-operation passed for user with username: {}", user.getUsername());

        findUser(user.getUsername(), true);
        logger.debug("Absence of user with username: {} in database check is passed", user.getUsername());

        List<Role> roles = roleRepository.findAllById(environmentValues.TODO_USER_ROLES);
        logger.debug("Roles to be assigned to user: {}", environmentValues.TODO_USER_ROLES);
        logger.debug("User roles fetched from the database: {}", roles);

        if (environmentValues.TODO_USER_ROLES.size() != roles.size()) {
            throw new RuntimeException("Roles not found in the database: " + roles);
        }

        logger.info("All validations for user-creation-operation passed for user with username: {}", user.getUsername());

        user
                .setPassword(passwordEncoder.encode(user.getPassword()))
                .setCreatedAt(LocalDateTime.parse(formatter.format(LocalDateTime.now())))
                .setRoles(roles);
        logger.debug("User password (encoded), creation time: {} and roles: {} have been set", user.getCreatedAt(), user.getRoles());

        user = userRepository.save(user);
        logger.info("User with username: {} has been saved to the database", user.getUsername());

        return user;
    }

    @Override
    public Map.Entry<User, Boolean> updatePassword(String username, PasswordUpdateRequest passwordUpdateRequest) {

        logger.debug("Parameters:: username: {}, passwordUpdateRequest: {}", username, passwordUpdateRequest);

        if (username == null) throw new RuntimeException("Username cannot be null");
        logger.debug("Not null check passed for username");

        passwordUpdateRequestValidator.validate(passwordUpdateRequest);
        logger.debug("Validation for user-password-update operation passed for passwordUpdateRequest object");

        User user = findUser(username, false).get();
        logger.debug("User with username: {} is found in the database", user.getUsername());

        Assert.isTrue(passwordEncoder.matches(passwordUpdateRequest.getCurrent().getPassword(), user.getPassword()), ConstantValues.User.Error.PASSWORD_MISMATCH);
        logger.debug("Current user password has been verified successfully");

        logger.info("All validations for user-password-update-operation passed for user with username: {}", user.getUsername());

        if (passwordEncoder.matches(passwordUpdateRequest.getModified().getPassword(), user.getPassword())) {
            logger.info("User password for username: {} has not been updated in the database as the new password is the same", user.getUsername());
            return Map.entry(user, false);
        }
        else {
            user.setPassword(passwordEncoder.encode(passwordUpdateRequest.getModified().getPassword()));
            logger.debug("New user password has been set");

            user = userRepository.save(user);
            logger.info("User with username: {} has been updated in the database", user.getUsername());

            return Map.entry(user, true);
        }
    }

    @Override
    public Map.Entry<User, Boolean> updateEmail(String username, User user) {

        logger.debug("Parameters:: username: {}, user: {}", username, user);

        if (username == null) throw new RuntimeException("Username cannot be null");
        logger.debug("Not null check passed for username");

        userValidator.validate(user, UserValidator.Operation.UpdateEmail);
        logger.debug("Validation for user-email-update operation passed for user object");

        User presentUser = findUser(username, false).get();
        logger.debug("User with username: {} has been found in the database", presentUser.getUsername());

        logger.info("All validations for user-email-update operation passed for user with username: {}", presentUser.getUsername());

        if (presentUser.getEmail().equals(user.getEmail())) {
            logger.info("Old email has not been replaced with: {} for user with username: {} as both emails are same", user.getEmail(), presentUser.getUsername());
            return Map.entry(presentUser, false);
        }
        else {
            presentUser.setEmail(user.getEmail());
            logger.debug("Old email has been replaced with: {} for user with username: {}", presentUser.getEmail(), presentUser.getUsername());

            presentUser = userRepository.save(presentUser);
            logger.info("User with username: {} has been updated in the database", presentUser.getUsername());

            return Map.entry(presentUser, true);
        }
    }

    @Override
    public Map.Entry<User, Boolean> updateUser(String username, User user) {

        logger.debug("Parameters:: username: {}, user: {}", username, user);

        if (username == null) throw new RuntimeException("Username cannot be null");
        logger.debug("Not null check passed for username");

        userValidator.validate(user, UserValidator.Operation.Update);
        logger.debug("Validation for user-update operation passed for user object");

        User oldUser = findUser(username, false).get();
        logger.debug("User with username: {} has been found in the database", oldUser.getUsername());

        logger.info("All validations for user-update operation passed for user with username: {}", oldUser.getUsername());

        boolean isUpdated = super.batchUpdate(oldUser, user);
        logger.debug("Executed batchUpdate method. Returned isUpdated: {}", isUpdated);

        if (isUpdated) {
            logger.debug("Updated User details has been set");

            oldUser = userRepository.save(oldUser);
            logger.info("User with username: {} has been updated in the database", oldUser.getUsername());
        }
        else
            logger.info("No user details has been updated as there is nothing to update");

        return Map.entry(oldUser, isUpdated);
    }

    @Override
    public User deleteUser(String username) {

        logger.debug("Parameters:: username: {}", username);

        if (username == null) throw new RuntimeException("Username cannot be null");
        logger.debug("Not null check passed for username");

        User user = findUser(username, false).get();
        logger.debug("User with username: {} has been found in the database", user.getUsername());

        logger.info("All validations for user-delete operation passed for user with username: {}", user.getUsername());

        userRepository.deleteById(username);
        logger.info("User with username: {} has been deleted from the database", user.getUsername());

        return user;
    }
}
