package com.example.todo.service;

import com.example.todo.model.PasswordUpdateRequest;
import com.example.todo.model.User;
import com.example.todo.exception.AlreadyExistException;
import com.example.todo.exception.NotFoundException;
import com.example.todo.annotation.BatchUpdatable;

import java.util.Map;

/**
 * An interface containing all the {@link User} related service methods.
 */
public interface UserService {

    /**
     * Finds a {@link User} by username and returns it.
     * @param username The username
     * @return The user
     * @throws NotFoundException if the user is not found
     */
    User findUser(String username);

    /**
     * Saves a {@link User} in the database.
     * @param user The user to be saved
     * @return The saved user
     * @throws AlreadyExistException if the user already exists
     */
    User createUser(User user);

    /**
     * Used for updating the password of a {@link User}. It also takes the present password and matches
     * it with the new password. If the passwords do not match, it is not updated.
     * @param username The username of the user
     * @param updateRequest The {@link PasswordUpdateRequest} with the new password and the present password
     * @return The updated user
     * @throws NotFoundException if the user is not found
     * @throws IllegalArgumentException if the passwords do not match
     */
    Map.Entry<User, Boolean> updatePassword(String username, PasswordUpdateRequest updateRequest);

    /**
     * Updates the email of a {@link User}.
     * @param username The username
     * @param email The new email
     * @return The updated user
     * @throws NotFoundException if the user is not found
     */
    Map.Entry<User, Boolean> updateEmail(String username, User user);

    /**
     * Used for updating the fields of a {@link User} that has been annotated with {@link BatchUpdatable} annotation.
     * If any of the fields is not present in the provided user (for example, if it's null), then it is not updated.
     * @param username The username of the user
     * @param user The user
     * @return The updated user to be saved
     * @throws NotFoundException if the user is not found
     */
    Map.Entry<User, Boolean> updateUser(String username, User user);

    /**
     * Deletes a {@link User} from the database.
     * @param username The username
     * @return The deleted user
     * @throws NotFoundException if the user is not found
     */
    User deleteUser(String username);
}
