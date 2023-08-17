package com.example.todoserver.web.controller.validation.group;

import com.example.todoserver.model.User;

/**
 * An interface to group different validation constraints together for different use cases for {@link User}.
 * It's used in validation api.
 */
public interface UserValidationGroup {
    
    interface Create {
    }

    interface UpdateEmail {
    }

    interface UpdatePassword {
    }

    interface Update {
    }
}
