package com.example.todoserver.service;

import com.example.todoserver.annotation.BatchUpdatable;
import com.example.todoserver.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.example.todoserver.model.User;
import com.example.todoserver.exception.NotFoundException;

/**
 * An interface containing all the {@link Todo} related service methods.
 */
public interface TodoService {

    /**
     * Finds a {@link Todo} by id that belongs to a {@link User} and returns it.
     * @param username The username of the user
     * @param id The id of the todoserver
     * @return The todoserver
     * @throws NotFoundException if the todoserver is not found or does not belong to the user
     */
    Todo findTodo(String username, UUID id);

    /**
     * Finds all the {@link Todo}s that belong to a {@link User} and returns them. Pagination can be done by
     * providing the page and limit and also sorting information. For sorting information, provide a list of
     * {@link Map.Entry}s with the field name as the key and {@link Sort.Direction} as the value. If sorting
     * information is not provided (i.e null), no sorting is done.
     * @param username The username of the user
     * @param page The page number
     * @param limit The limit of each page
     * @param orders The sorting information
     * @return The {@link Page} of todos
     * @throws NotFoundException if the user is not found
     */
    Page<Todo> findTodosByUsername(String username, int page, int limit, List<Map.Entry<String, Sort.Direction>> orders);

    /**
     * Saves a new {@link Todo} for a {@link User} in the database and returns it.
     * @param username The username
     * @param todo The todoserver
     * @return The saved todoserver
     * @throws NotFoundException if the user is not found
     */
    Todo createTodo(String username, Todo todo);

    /**
     * Used for updating the fields of a {@link Todo}, of a {@link User}, that has been annotated with
     * {@link BatchUpdatable} annotation. If any of the fields is not present in the provided todoserver
     * (for example, if it's null), then it is not updated.
     * @param username The username
     * @param id The id of the todoserver
     * @param todo The updated todoserver to be saved
     * @return The updated todoserver
     * @throws NotFoundException if the user or todoserver is not found or if the todoserver does not belong to the user
     */
    Todo updateTodo(String username, UUID id, Todo todo);

    /**
     * Deletes a {@link Todo} of a {@link User} from the database.
     * @param username The username
     * @param id The id of the todoserver
     * @return The deleted todoserver
     * @throws NotFoundException if the user or todoserver is not found or if the todoserver does not belong to the user
     */
    Todo deleteTodo(String username, UUID id);
}
