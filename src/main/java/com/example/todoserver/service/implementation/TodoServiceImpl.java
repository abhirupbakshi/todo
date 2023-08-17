package com.example.todoserver.service.implementation;

import com.example.todoserver.configuration.ConstantValues;
import com.example.todoserver.exception.AlreadyExistException;
import com.example.todoserver.exception.NotFoundException;
import com.example.todoserver.model.Todo;
import com.example.todoserver.model.User;
import com.example.todoserver.repository.TodoRepository;
import com.example.todoserver.repository.UserRepository;
import com.example.todoserver.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
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

    private TodoRepository todoRepository;
    private UserRepository userRepository;

    @Autowired
    public void setTodoRepository(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Sort createSort(List<Map.Entry<String, Sort.Direction>> orders) {

        Sort sort = null;

        for (Map.Entry<String, Sort.Direction> order : orders) {

            String property = order.getKey();
            Sort.Direction direction = order.getValue();

            Sort temp = Sort.by(new Sort.Order(direction, property));
            sort = sort == null ? temp : sort.and(temp);
        }

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
     * Finds a todoserver with the given id and a username and returns an {@link Optional} {@link Todo}. second parameter is an optional boolean parameter
     * throwException that indicates if and when an exception should be thrown. If throwException is true and the todoserver
     * exists, then an {@link AlreadyExistException} is thrown. If throwException is false and the tod does not exist,
     * then a {@link NotFoundException} is thrown. If throwException is null, then the third parameter, matchUsername, is checked, which indicates
     * whether the todoserver belongs to a specific user or not, judged by the given username. if matchUsername is true and the todoserver does not belong
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
        return findTodo(username, id, false, true).get();
    }

    @Override
    public Page<Todo> findTodosByUsername(String username, int pageNo, int limit, List<Map.Entry<String, Sort.Direction>> orders) {

        Pageable pageable;
        List<Todo> todos;

        if (orders == null)
            pageable = PageRequest.of(--pageNo, limit, Sort.unsorted());
        else
            pageable = PageRequest.of(--pageNo, limit, createSort(orders));

        findUser(username, false);

        todos = todoRepository
                .findByUsername(username, pageable)
                .getContent()
                .stream()
                .toList();

        return new PageImpl<>(todos, pageable, todos.size());
    }

    @Override
    public Todo createTodo(String username, Todo todo) {

        User user = findUser(username, false).get();

        todo.setId(UUID.randomUUID());
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        todo.setUser(user);

        return todoRepository.save(todo);
    }

    @Override
    public Todo updateTodo(String username, UUID id, Todo todo) {

        Todo todoEntity = findTodo(username, id, false, true).get();
        boolean isUpdated = super.batchUpdate(todoEntity, todo);

        if (isUpdated)
            todoEntity.setUpdatedAt(LocalDateTime.now());

        return todoRepository.save(todoEntity);
    }

    @Override
    public Todo deleteTodo(String username, UUID id) {

        Todo todo = findTodo(username, id, false, true).get();

        todoRepository.deleteById(id);

        return todo;
    }
}
