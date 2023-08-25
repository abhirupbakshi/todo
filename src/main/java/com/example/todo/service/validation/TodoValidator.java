package com.example.todo.service.validation;

import com.example.todo.model.Todo;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Map;

/**
 * An interface for validation of {@link Todo} objects
 */
public interface TodoValidator {

    /**
     * The different operations that can be performed on a {@link Todo}
     */
    enum Operation {
        Create,
        Update
    }

    /**
     * Validates a {@link Todo} object according to the {@link TodoValidator.Operation} passed. Each operation has
     * predefined validation rules, implemented using java validation api.
     * @param todo The todo to be validated.
     * @param operation The operation to be performed.
     */
    void validate(Todo todo, Operation operation);

    /**
     * Takes a list of sort by parameters based on the json representation of {@link Todo} and a list of order parameters,
     * and returns a list of {@link Map.Entry}s, containing the sort by parameters according to the java variable names
     * and {@link Sort.Direction}s.
     * @param sort A {@link List} of sort by parameters
     * @param order A {@link List} of order parameters. should be either "asc" or "desc"
     * @return A {@link List} of {@link Map.Entry}s of sort by parameters and {@link Sort.Direction}s
     * @throws IllegalArgumentException If the count of sort and order parameters do not match,
     * or one of the parameters is null, or one of the parameters has invalid value.
     */
    public List<Map.Entry<String, Sort.Direction>> validateAndCreateSortOrders(List<String> sort, List<String> order);
}
