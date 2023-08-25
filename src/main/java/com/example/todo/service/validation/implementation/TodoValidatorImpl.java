package com.example.todo.service.validation.implementation;

import com.example.todo.configuration.ConstantValues;
import com.example.todo.model.Todo;
import com.example.todo.model.validation.ValidationGroup;
import com.example.todo.service.validation.TodoValidator;
import com.example.todo.utility.JsonUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An implementation of {@link TodoValidator}
 */
@Component
public class TodoValidatorImpl extends AbstractModelValidator<Todo> implements TodoValidator {

    private final Logger logger = LoggerFactory.getLogger(TodoValidatorImpl.class);
    private JsonUtilities jsonUtility;

    @Autowired
    public void setJsonUtility(JsonUtilities jsonUtility) {
        this.jsonUtility = jsonUtility;
    }

    @Override
    public void validate(Todo todo, Operation operation) {

        logger.debug("Parameters:: operation: {} and todo: {}", operation, todo);

        Assert.notNull(todo, ConstantValues.RestApi.Error.REQUEST_BODY_NEEDED);
        logger.debug("Not null check passed for Todo object");

        if (operation == null) throw new RuntimeException("Operation cannot be null");
        logger.debug("Not null check passed for Operation object");

        switch (operation) {
            case Create -> handleConstraintViolation(todo, ValidationGroup.Todo.Create.class);
            case Update -> handleConstraintViolation(todo, ValidationGroup.Todo.Update.class);
        }

        logger.info("Validation completed for Todo object");
    }

    @Override
    public List<Map.Entry<String, Sort.Direction>> validateAndCreateSortOrders(List<String> sort, List<String> order) {

        logger.debug("Parameters:: sort: {} and order: {}", sort, order);

        List<Map.Entry<String, Sort.Direction>> orders = null;

        if ((sort == null ^ order == null) || (sort != null && sort.size() != order.size())) {
            throw new IllegalArgumentException(ConstantValues.Todo.Error.INVALID_SORT_ORDER_PARAMETER);
        }
        else if (sort != null) {

            logger.debug("Sort and Order parameters are not null and sort.size == order.size");

            orders = new ArrayList<>();
            logger.debug("Orders list (List<Map.Entry<String, Sort.Direction>>) has been initialized");

            for (int i = 0; i < sort.size(); i++) {
                if (
                        sort.get(i) == null || order.get(i) == null ||
                        sort.get(i).isEmpty() || order.get(i).isEmpty() ||
                        (!order.get(i).equalsIgnoreCase("asc") && !order.get(i).equalsIgnoreCase("desc"))
                ) {
                    throw new IllegalArgumentException(ConstantValues.Todo.Error.INVALID_SORT_ORDER_PARAMETER);
                }

                String _sort = jsonUtility.toClassFields(sort.get(i), Todo.class);
                Sort.Direction _order = order.get(i).equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
                logger.debug("Generated sort by: {} and sort order: {} for index: {}", _sort, _order, i);

                Assert.notNull(_sort, ConstantValues.Todo.Error.INVALID_SORT_ORDER_PARAMETER);
                logger.debug("Not null check passed for sort by");

                orders.add(Map.entry(_sort, _order));
                logger.debug("Added sort by and sort order to orders list");
            }
        }

        logger.info("Generated orders list: {}", orders);

        return orders;
    }
}
