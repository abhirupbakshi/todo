package com.example.todoserver.web.controller.validation.group;

import com.example.todoserver.model.Todo;

/**
 * An interface to group different validation constraints together for different use cases for {@link Todo}.
 * It's used in validation api.
 */
public interface TodoValidationGroup {

    interface Create {
    }
    interface Update {
    }
}
