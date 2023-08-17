package com.example.todoserver.service.implementation;

import com.example.todoserver.configuration.Constants;
import com.example.todoserver.exception.AlreadyExistException;
import com.example.todoserver.exception.NotFoundException;
import com.example.todoserver.model.User;
import com.example.todoserver.repository.UserRepository;
import com.example.todoserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * An implementation of {@link UserService}.
 */
@Service
public class UserServiceImpl extends AbstractModelServiceImpl<User> implements UserService {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
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
            throw new AlreadyExistException(Constants.User.Error.USER_EXISTS);
        else if (throwException != null && !throwException && user.isEmpty()) // Throw exception if not present
            throw new NotFoundException(Constants.User.Error.USER_NOT_FOUND);

        return user;
    }

    @Override
    public User findUser(String username) {
        return findUser(username, false).get();
    }

    @Override
    public User createUser(User user) {

        findUser(user.getUsername(), true);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles(Constants.User.USER_ROLES);

        return userRepository.save(user);
    }

    @Override
    public User updatePassword(String username, String currentPassword, String newPassword) {

        User user = findUser(username, false).get();

        Assert.isTrue(passwordEncoder.matches(currentPassword, user.getPassword()), Constants.User.Error.PASSWORD_MISMATCH);
        user.setPassword(passwordEncoder.encode(newPassword));

        return userRepository.save(user);
    }

    @Override
    public User updateEmail(String username, String email) {

        User user = findUser(username, false).get().setEmail(email);

        return userRepository.save(user);
    }

    @Override
    public User updateUser(String username, User user) {

        User userEntity = findUser(username, false).get();

        super.batchUpdate(userEntity, user);

        return userRepository.save(userEntity);
    }

    @Override
    public User deleteUser(String username) {

        User user = findUser(username, false).get();

        userRepository.deleteById(username);

        return user;
    }
}
