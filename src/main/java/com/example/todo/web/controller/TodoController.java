package com.example.todo.web.controller;

import com.example.todo.configuration.ConstantValues;
import com.example.todo.model.Todo;
import com.example.todo.service.validation.implementation.TodoValidatorImpl;
import com.example.todo.service.TodoService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping(ConstantValues.RestApi.REST_API_ROUTE_PREFIX + "/todos")
public class TodoController {

    private final Logger logger = LoggerFactory.getLogger(TodoController.class);
    private TodoService todoService;
    private TodoValidatorImpl todoValidatorImpl;

    @Autowired
    public void setTodoService(TodoService todoService) {
        this.todoService = todoService;
    }

    @Autowired
    public void setTodoValidator(TodoValidatorImpl todoValidatorImpl) {
        this.todoValidatorImpl = todoValidatorImpl;
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Todo> findTodo(@PathVariable("id") UUID id, Principal principal, HttpServletRequest request) {

        Todo todo = todoService.findTodo(principal.getName(), id);
        logger.info("Found todo with id: {}, username: {} at request url: {}", todo.getId(), principal.getName(), request.getRequestURL());

        return new ResponseEntity<>(todo, HttpStatus.OK);
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Todo>> findTodosByUsername(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                          @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                                          @RequestParam(value = "sort", required = false) List<String> sort,
                                                          @RequestParam(value = "order", required = false) List<String> order,
                                                          Principal principal, HttpServletRequest request) {

        Page<Todo> todos = todoService.findTodosByUsername(principal.getName(), page, limit, todoValidatorImpl.validateAndCreateSortOrders(sort, order));
        logger.info("Found {} todos for username: {} at request url: {} with page: {}, limit: {}, sort: {}, order: {}",
                todos.getTotalElements(), principal.getName(), request.getRequestURL(), page, limit, sort, order);

        HttpHeaders headers = new HttpHeaders();
        headers.set(ConstantValues.RestApi.PAGINATION_TOTAL_COUNT_HEADER, String.valueOf(todos.getTotalElements()));
        logger.debug("Added response header {}:{}", ConstantValues.RestApi.PAGINATION_TOTAL_COUNT_HEADER, todos.getTotalElements());

        return new ResponseEntity<>(todos.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo, Principal principal, HttpServletRequest request) {

        todo = todoService.createTodo(principal.getName(), todo);
        logger.info("Created todo with id {}, username: {} at request url: {}", todo.getId(), principal.getName(), request.getRequestURL());

        return new ResponseEntity<>(todo, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Todo> updateTodo(@PathVariable("id")  UUID id, @RequestBody Todo todo, Principal principal, HttpServletRequest request) {

        todo = todoService.updateTodo(principal.getName(), id, todo);
        logger.info("Updated todo with id: {}, username: {} at request url: {}", todo.getId(), principal.getName(), request.getRequestURL());

        return new ResponseEntity<>(todo, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Todo> deleteTodo(@PathVariable("id") UUID id, Principal principal, HttpServletRequest request) {

        Todo todo = todoService.deleteTodo(principal.getName(), id);
        logger.info("Deleted todo with id: {}, username: {} at request url: {}", todo.getId(), principal.getName(), request.getRequestURL());

        return new ResponseEntity<>(todo, HttpStatus.OK);
    }
}
