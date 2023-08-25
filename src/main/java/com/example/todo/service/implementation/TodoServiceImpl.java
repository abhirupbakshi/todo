package com.example.todo.service.implementation;

import com.example.todo.configuration.ConstantValues;
import com.example.todo.exception.AlreadyExistException;
import com.example.todo.exception.NotFoundException;
import com.example.todo.model.Todo;
import com.example.todo.model.User;
import com.example.todo.service.validation.TodoValidator;
import com.example.todo.service.validation.implementation.TodoValidatorImpl;
import com.example.todo.repository.TodoRepository;
import com.example.todo.repository.UserRepository;
import com.example.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * An implementation of {@link TodoService}.
 */
@Service
public class TodoServiceImpl extends AbstractModelServiceImpl<Todo> implements TodoService {

    private final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);
    private TodoRepository todoRepository;
    private UserRepository userRepository;
    private TodoValidator todoValidator;

    @Autowired
    public void setTodoRepository(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setTodoValidator(TodoValidatorImpl todoValidatorImpl) {
        this.todoValidator = todoValidatorImpl;
    }

    private Sort createSort(List<Map.Entry<String, Sort.Direction>> orders) {

        logger.debug("Parameters:: orders: {}", orders);

        Sort sort = null;

        for (Map.Entry<String, Sort.Direction> order : orders) {

            String property = order.getKey();
            Sort.Direction direction = order.getValue();

            Sort temp = Sort.by(new Sort.Order(direction, property));
            sort = sort == null ? temp : sort.and(temp);
        }

        logger.debug("Created Sort object: {}", sort);

        return sort;
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

    /**
     * Finds a todo with the given id and a username and returns an {@link Optional} {@link Todo}. second parameter is an optional boolean parameter
     * throwException that indicates if and when an exception should be thrown. If throwException is true and the todo
     * exists, then an {@link AlreadyExistException} is thrown. If throwException is false and the tod does not exist,
     * then a {@link NotFoundException} is thrown. If throwException is null, then the third parameter, matchUsername, is checked, which indicates
     * whether the todo belongs to a specific user or not, judged by the given username. if matchUsername is true and the todo does not belong
     * to the given user, then an {@link NotFoundException} is thrown.
     * @param username The username
     * @param throwException Boolean, indicating if an exception should be thrown and when.
     * @return The Optional user
     */
    private Optional<Todo> findTodo(String username, UUID todoId, Boolean throwException, boolean matchUsername) {

        Optional<Todo> todo = todoRepository.findById(todoId);

        if (throwException != null && throwException && todo.isPresent()) // Throw exception if present
            throw new AlreadyExistException(ConstantValues.Todo.Error.TODO_EXISTS);
        else if (throwException != null && !throwException && todo.isEmpty()) // Throw exception if not present
            throw new NotFoundException(ConstantValues.Todo.Error.TODO_NOT_FOUND);

        if (matchUsername && todo.isPresent() && !todo.get().getUser().getUsername().equals(username))
            throw new NotFoundException(ConstantValues.Todo.Error.TODO_NOT_FOUND);

        return todo;
    }

    @Override
    public Todo findTodo(String username, UUID id) {

        logger.debug("Parameters:: username: {}, id: {}", username, id);

        if (username == null) throw new RuntimeException("Username cannot be null");
        logger.debug("Not null check passed for username");

        if (id == null) throw new RuntimeException("Id cannot be null");
        logger.debug("Not null check passed for id");

        Todo todo = findTodo(username, id, false, true).get();
        logger.info("Todo with id: {}, username: {} has been found", todo.getId(), todo.getUser().getUsername());

        return todo;
    }

    @Override
    public Page<Todo> findTodosByUsername(String username, int pageNo, int limit, List<Map.Entry<String, Sort.Direction>> orders) {

        logger.debug("Parameters:: username: {}, page no: {}, limit: {}, orders: {}", username, pageNo, limit, orders);

        if (username == null) throw new RuntimeException("Username cannot be null");
        logger.debug("Not null check passed for username");

        Assert.isTrue(pageNo > 0, ConstantValues.Todo.Error.INVALID_PAGE_NO);
        logger.debug("Page no validity check passed");

        Assert.isTrue(limit > 0, ConstantValues.Todo.Error.INVALID_PAGE_LIMIT);
        logger.debug("Page limit validity check passed");

        findUser(username, false);
        logger.debug("User with username: {} has been found in the database", username);

        logger.info("::Skipping validation check for sorting information::");
        logger.info("All validity checks passed for finding todos for user with username: {}", username);

        Pageable pageable;
        List<Todo> todos;

        if (orders == null)
            pageable = PageRequest.of(--pageNo, limit, Sort.unsorted());
        else
            pageable = PageRequest.of(--pageNo, limit, createSort(orders));
        logger.debug("Created Pageable object: {}", pageable);

        todos = todoRepository
                .findByUsername(username, pageable)
                .getContent()
                .stream()
                .toList();
        logger.info("Todos for user with username: {} have been fetched from the database", username);

        PageImpl<Todo> page = new PageImpl<>(todos, pageable, todos.size());
        logger.debug("Created Page object: {}", page);

        logger.info("Found {} todos for user with username: {} at page no: {} with limit: {} and sort orders: {}", page.getTotalElements(), username, pageNo, limit, orders);

        return page;
    }

    @Override
    public Todo createTodo(String username, Todo todo) {

        logger.debug("Parameters:: username: {}, todo: {}", username, todo);

        if (username == null) throw new RuntimeException("Username cannot be null");
        logger.debug("Not null check passed for username");

        todoValidator.validate(todo, TodoValidator.Operation.Create);
        logger.debug("Validation passed for create-todo operation, for given todo object");

        User user = findUser(username, false).get();
        logger.debug("User with username: {} has been found in the database", username);

        logger.info("All validation checks passed for create-todo operation for user with username: {}", username);

        todo
                .setId(UUID.randomUUID())
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setUser(user);
        logger.debug("Todo id: {}, created at: {}, updated at: {}, user with username: {} has been set",
                todo.getId(), todo.getCreatedAt(), todo.getUpdatedAt(), todo.getUser().getUsername());

        todo = todoRepository.save(todo);
        logger.info("Todo with id: {} for user with username: {} has been saved in the database", todo.getId(), todo.getUser().getUsername());

        return todo;
    }

    @Override
    public Todo updateTodo(String username, UUID id, Todo todo) {

        logger.debug("Parameters:: username: {}, id: {}, todo: {}", username, id, todo);

        if (username == null) throw new RuntimeException("Username cannot be null");
        logger.debug("Not null check passed for username");

        if (id == null) throw new RuntimeException("Id cannot be null");
        logger.debug("Not null check passed for id");

        todoValidator.validate(todo, TodoValidator.Operation.Update);
        logger.debug("Validation passed for update-todo operation");

        Todo databaseTodo = findTodo(username, id, false, true).get();
        logger.debug("Todo with id: {} for user with username: {} has been found in the database", id, username);

        logger.info("All validation checks passed for update-todo operation for todo with id: {}, username: {}", id, username);

        boolean isUpdated = super.batchUpdate(databaseTodo, todo);
        logger.debug("Executed batchUpdate method. Returned isUpdated: {}", isUpdated);

        if (isUpdated) {
            databaseTodo.setUpdatedAt(LocalDateTime.now());
            logger.debug("Updated Todo information has been set");

            databaseTodo = todoRepository.save(databaseTodo);
            logger.info("Todo with id: {} for user with username: {} has been saved in the database", databaseTodo.getId(), databaseTodo.getUser().getUsername());
        }
        else
            logger.info("No todo information has been updated as there is nothing to update");

        return databaseTodo;
    }

    @Override
    public Todo deleteTodo(String username, UUID id) {

        logger.debug("Parameters:: username: {}, id: {}", username, id);

        if (username == null) throw new RuntimeException("Username cannot be null");
        logger.debug("Not null check passed for username");

        if (id == null) throw new RuntimeException("Id cannot be null");
        logger.debug("Not null check passed for id");

        Todo todo = findTodo(username, id, false, true).get();
        logger.debug("Todo with id: {} for user with username: {} has been found in the database", id, username);

        logger.info("All validation checks passed for delete-todo operation for todo with id: {}, username: {}", id, username);

        todoRepository.deleteById(id);
        logger.info("Todo with id: {} for user with username: {} has been deleted from the database", id, username);

        return todo;
    }
}
