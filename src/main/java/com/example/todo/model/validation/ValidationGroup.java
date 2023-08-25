package com.example.todo.model.validation;

/**
 * An interface to group different validation constraints together for different models. It's used in validation api.
 */
public interface ValidationGroup {
    /**
     * An interface to group different validation constraints together for different use cases for {@link com.example.todo.model.User}.
     * It's used in validation api.
     */
    interface User {

        interface Create {
        }

        interface UpdateEmail {
        }

        interface UpdatePassword {
        }

        interface Update {
        }
    }

    /**
     * An interface to group different validation constraints together for different use cases for {@link com.example.todo.model.Todo}.
     * It's used in validation api.
     */
    interface Todo {

        interface Create {
        }
        interface Update {
        }
    }
}
