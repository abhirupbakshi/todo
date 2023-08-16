package com.example.server.web.controller;

import com.example.server.configuration.Constants;
import com.example.server.model.Todo;
import com.example.server.service.TodoService;
import com.example.server.utility.JsonUtilities;
import com.example.server.web.controller.validation.group.TodoValidationGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping(Constants.RestApi.REST_API_ROUTE_PREFIX + "/todos")
public class TodoController {

    private TodoService todoService;
    private JsonUtilities jsonUtility;

    @Autowired
    public void setTodoService(TodoService todoService) {
        this.todoService = todoService;
    }

    @Autowired
    public void setJsonUtility(JsonUtilities jsonUtility) {
        this.jsonUtility = jsonUtility;
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Todo> findTodo(@PathVariable("id") UUID id, Principal principal) {

        Todo todo = todoService.findTodo(principal.getName(), id);

        return new ResponseEntity<>(todo, HttpStatus.OK);
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Todo>> findTodosByUsername(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                          @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                                          @RequestParam(value = "sort", required = false) List<String> sort,
                                                          @RequestParam(value = "order", required = false) List<String> order,
                                                          Principal principal) {

        Assert.isTrue(page > 0, Constants.Todo.Error.INVALID_PAGE_NO);
        Assert.isTrue(limit > 0, Constants.Todo.Error.INVALID_PAGE_LIMIT);

        List<Map.Entry<String, Sort.Direction>> orders = null;

        if (
                (sort == null ^ order == null) ||
                (sort != null && sort.size() != order.size())) {

                throw new IllegalArgumentException(Constants.Todo.Error.INVALID_SORT_ORDER_PARAMETER);
        }
        else if (sort != null) {

            orders = new ArrayList<>();

            for (int i = 0; i < sort.size(); i++) {
                if (
                        sort.get(i) == null || order.get(i) == null ||
                        sort.get(i).isEmpty() || order.get(i).isEmpty() ||
                        (!order.get(i).equalsIgnoreCase("asc") && !order.get(i).equalsIgnoreCase("desc"))
                ) {
                    throw new IllegalArgumentException(Constants.Todo.Error.INVALID_SORT_ORDER_PARAMETER);
                }

                String _sort = jsonUtility.toClassFields(sort.get(i), Todo.class);
                Sort.Direction _order = order.get(i).equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

                Assert.notNull(_sort, Constants.Todo.Error.INVALID_SORT_ORDER_PARAMETER);

                orders.add(Map.entry(_sort, _order));
            }
        }

        Page<Todo> todos = todoService.findTodosByUsername(principal.getName(), page, limit, orders);
        HttpHeaders headers = new HttpHeaders();

        headers.set("X-Total-Count", String.valueOf(todos.getTotalElements()));

        return new ResponseEntity<>(todos.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Todo> createTodo(@Validated(TodoValidationGroup.Create.class) @RequestBody Todo todo, Principal principal) {

        Todo created = todoService.createTodo(principal.getName(), todo);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Todo> updateTodo(@PathVariable("id")  UUID id, @Validated(TodoValidationGroup.Update.class) @RequestBody Todo todo, Principal principal) {

        todo = todoService.updateTodo(principal.getName(), id, todo);

        return new ResponseEntity<>(todo, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Todo> deleteTodo(@PathVariable("id") UUID id, Principal principal) {

        Todo todo = todoService.deleteTodo(principal.getName(), id);

        return new ResponseEntity<>(todo, HttpStatus.OK);
    }
}
