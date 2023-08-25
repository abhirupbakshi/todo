package com.example.todo.configuration;

import org.springframework.http.HttpHeaders;

/**
 * A set of constants used throughout the application, like JWT keys, user roles, error messages, etc.
 * This class centralizes all the constants in one place. It also fetches environment variables values.
 */
public final class ConstantValues {

    public static final class RestApi {
        public static final String REST_API_ROUTE_PREFIX = "/v1";
        public static final String PAGINATION_TOTAL_COUNT_HEADER = "X-Total-Count";

        public static final class Error {
            public static final String REQUEST_BODY_NEEDED = "Request body is needed";
            public static final String AUTHORIZATION_HEADER_NOT_FOUND = HttpHeaders.AUTHORIZATION + " header is not found";
            public static final String AUTHORIZATION_HEADER_INVALID = "Invalid " + HttpHeaders.AUTHORIZATION + " header";
        }
    }

    public static final class Jwt {
        public static final String BEARER_TOKEN_PREFIX = "Bearer";
        public static final String RESPONSE_HEADER_TOKEN = "Auth-Token";
        public static final String REQUEST_ATTRIBUTE_TOKEN_KEY = "TOKEN";
        public static final class Error {
            public static final String INVALID_TOKEN = "Token is invalid";
        }
    }

    public static final class User {
        public static final int USERNAME_MIN_LENGTH = 5;
        public static final int USERNAME_MAX_LENGTH = 60;
        public static final int PASSWORD_MIN_LENGTH = 8;
        public static final int PASSWORD_MAX_LENGTH = 255;
        public static final int EMAIL_MAX_LENGTH = 50;
        public static final int FORENAME_MIN_LENGTH = 2;
        public static final int FORENAME_MAX_LENGTH = 50;
        public static final int SURNAME_MIN_LENGTH = 2;
        public static final int SURNAME_MAX_LENGTH = 50;
        public static final class Json {
            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
            public static final String EMAIL = "email";
            public static final String CREATED_AT = "created_at";
            public static final String FORENAME = "forename";
            public static final String SURNAME = "surname";
        }
        public static final class Error {
            public static final String CREDENTIALS_NEEDED = "User credentials are needed";
            public static final String USERNAME_NEEDED = "Username is needed";
            public static final String PASSWORD_NEEDED = "Password is needed";
            public static final String EMAIL_NEEDED = "Email is needed";
            public static final String FORENAME_NEEDED = "Forename is needed";
            public static final String SURNAME_NEEDED = "Surname is needed";
            public static final String PASSWORD_MISMATCH = "Password is invalid";
            public static final String PASSWORD_INVALID_LENGTH = "Password must be between " + PASSWORD_MIN_LENGTH + " and " + PASSWORD_MAX_LENGTH + " characters (inclusive)";
            public static final String USERNAME_INVALID_LENGTH = "Username must be between " + USERNAME_MIN_LENGTH + " and " + USERNAME_MAX_LENGTH + " characters (inclusive)";
            public static final String EMAIL_IS_INVALID = "Email should be valid";
            public static final String EMAIL_IS_TOO_LONG = "Email should not be more than " + EMAIL_MAX_LENGTH + " characters";
            public static final String FORENAME_IS_INVALID = "Forename must be between " + FORENAME_MIN_LENGTH + " and " + FORENAME_MAX_LENGTH + " characters (inclusive)";
            public static final String SURNAME_IS_INVALID = "Surname must be between " + SURNAME_MIN_LENGTH + " and " + SURNAME_MAX_LENGTH + " characters (inclusive)";
            public static final String USER_NOT_FOUND = "User not found";
            public static final String USER_EXISTS = "User already exists";
        }
    }

    public static final class PasswordUpdateRequest {
        public static final class Json {
            public static final String CURRENT = "current";
            public static final String MODIFIED = "modified";
        }
        public static final class Error {
            public static final String CURRENT_NEEDED = "Current user's password is needed";
            public static final String MODIFIED_NEEDED = "Modified user's password is needed";
        }
    }

    public static final class Todo {
        public static final int TITLE_MAX_LENGTH = 300;
        public static final int DESCRIPTION_MAX_LENGTH = 500;
        public static final class Json {
            public static final String ID = "id";
            public static final String TITLE = "title";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
            public static final String SCHEDULED_AT = "scheduled_at";
            public static final String COMPLETED = "completed";
            public static final String DESCRIPTION = "description";
            public static final String USER = "user";
        }
        public static final class Error {
            public static final String INVALID_PAGE_NO = "Page number must be greater than 0";
            public static final String INVALID_PAGE_LIMIT = "Limit must be greater than 0";
            public static final String INVALID_SORT_ORDER_PARAMETER = "Invalid sort or order parameters";
            public static final String TITLE_NEEDED = "Title is needed";
            public static final String SCHEDULED_AT_NEEDED = "Scheduled at time is needed";
            public static final String DESCRIPTION_NEEDED = "Description is needed";
            public static final String COMPLETED_NEEDED = "Completed is needed";
            public static final String TITLE_IS_INVALID = "Title must be between 1 and " + TITLE_MAX_LENGTH + " characters (inclusive)";
            public static final String SCHEDULED_AT_SHOULD_BE_PRESENT_FUTURE = "Scheduled at time needs to be in present or future";
            public static final String COMPLETED_IS_INVALID = "Completed field must be either true or false (ignoring case)";
            public static final String Scheduled_At_Field_IS_INVALID = "Cannot parse scheduled at time";
            public static final String DESCRIPTION_IS_INVALID = "Description must be between 1 and " + DESCRIPTION_MAX_LENGTH + " characters (inclusive)";
            public static final String TODO_NOT_FOUND = "Todo not found";
            public static final String TODO_EXISTS = "Todo already exists";
        }
    }
}
