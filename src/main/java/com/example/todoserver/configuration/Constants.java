package com.example.todoserver.configuration;

import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.List;

/**
 * A set of constants used throughout the application, like JWT keys, user roles, error messages, etc.
 * This class centralizes all the constants in one place. It also fetches environment variables values.
 */
public final class Constants {

    public static abstract class RestApi {

        public static final String REST_API_ROUTE_PREFIX = "/api/v1";
        public static final List<String> CORS_ALLOWED_ORIGINS;
        public static final List<String> CORS_ALLOWED_METHODS = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");
        public static final List<String> CORS_ALLOWED_HEADERS = List.of("Authorization", "Content-Type", "X-Requested-With", "Accept");

        static {

//            CORS_ALLOWED_ORIGINS = Arrays.stream(System.getenv("CORS_ALLOWED_ORIGINS").split(" ")).toList();
            CORS_ALLOWED_ORIGINS = List.of("*");
        }
    }

    public static abstract class Jwt {

        public static final Key SIGNING_KEY = Keys.hmacShaKeyFor(System.getenv("JWT_SECRET").getBytes());
        public static final long EXPIRATION_TIME_IN_SECONDS = Long.parseLong(System.getenv("JWT_EXPIRATION_TIME_IN_SECONDS"));
        public static final String BEARER_TOKEN_PREFIX = "Bearer";
        public static final String REQUEST_ATTRIBUTE_TOKEN_KEY = "TOKEN";

        public static abstract class Error {

            public static final String INVALID_TOKEN = "Token is invalid";
        }
    }

    public static abstract class User {

        public static final List<String> USER_ROLES = List.of(Arrays.stream(System.getenv("USER_ROLES").split(":")).toArray(String[]::new));
        public static final int USERNAME_MIN_LENGTH = 5;
        public static final int USERNAME_MAX_LENGTH = 60;
        public static final int PASSWORD_MIN_LENGTH = 8;
        public static final int PASSWORD_MAX_LENGTH = 255;
        public static final int EMAIL_MAX_LENGTH = 50;
        public static final int FORENAME_MIN_LENGTH = 2;
        public static final int FORENAME_MAX_LENGTH = 50;
        public static final int SURNAME_MIN_LENGTH = 2;
        public static final int SURNAME_MAX_LENGTH = 50;

        public static abstract class Json {

            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
            public static final String EMAIL = "email";
            public static final String CREATED_AT = "created_at";
            public static final String FORENAME = "forename";
            public static final String SURNAME = "surname";
        }

        public static abstract class Error {

            public static final String USERNAME_NEEDED = "Username is needed";
            public static final String PASSWORD_NEEDED = "Password is needed";
            public static final String EMAIL_NEEDED = "Email is needed";
            public static final String FORENAME_NEEDED = "Forename is needed";
            public static final String SURNAME_NEEDED = "Surname is needed";
            public static final String PASSWORD_MISMATCH = "Password is invalid";
            public static final String PASSWORD_INVALID_LENGTH = "Password must be between " + PASSWORD_MIN_LENGTH + " and " + PASSWORD_MAX_LENGTH + " characters (inclusive)";
            public static final String USERNAME_INVALID_LENGTH = "Username must be between " + USERNAME_MIN_LENGTH + " and " + USERNAME_MAX_LENGTH + " characters (inclusive)";
            public static final String EMAIL_IS_INVALID = "Email should be valid and must not be more than " + EMAIL_MAX_LENGTH + " characters";
            public static final String FORENAME_IS_INVALID = "Forename must be between " + FORENAME_MIN_LENGTH + " and " + FORENAME_MAX_LENGTH + " characters (inclusive)";
            public static final String SURNAME_IS_INVALID = "Surname must be between " + SURNAME_MIN_LENGTH + " and " + SURNAME_MAX_LENGTH + " characters (inclusive)";
            public static final String USER_NOT_FOUND = "User not found";
            public static final String USER_EXISTS = "User already exists";
        }
    }

    public static abstract class PasswordUpdateRequest {

        public static abstract class Json {

            public static final String CURRENT = "current";
            public static final String MODIFIED = "modified";
        }

        public static abstract class Error {

            public static final String CURRENT_NEEDED = "Current user's password is needed";
            public static final String MODIFIED_NEEDED = "Modified user's password is needed";
        }
    }

    public static abstract class Todo {

        public static final int TITLE_MAX_LENGTH = 255;
        public static final int DESCRIPTION_MAX_LENGTH = 500;

        public static abstract class Json {

            public static final String ID = "id";
            public static final String TITLE = "title";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
            public static final String SCHEDULED_AT = "scheduled_at";
            public static final String COMPLETED = "completed";
            public static final String DESCRIPTION = "description";
            public static final String USER = "user";
        }

        public static abstract class Error {

            public static final String INVALID_PAGE_NO = "Page number must be greater than 0";
            public static final String INVALID_PAGE_LIMIT = "Limit must be greater than 0";
            public static final String INVALID_SORT_ORDER_PARAMETER = "Invalid sort or order parameters";
            public static final String TITLE_NEEDED = "Title is needed";
            public static final String SCHEDULED_AT_NEEDED = "Scheduled at time is needed";
            public static final String DESCRIPTION_NEEDED = "Description is needed";
            public static final String COMPLETED_NEEDED = "Completed is needed";
            public static final String TITLE_IS_INVALID = "Title must be between 1 and " + TITLE_MAX_LENGTH + " characters (inclusive)";
            public static final String SCHEDULED_AT_IS_INVALID = "Scheduled at time needs to be in present or future";
            public static final String COMPLETED_IS_INVALID = "Completed field must be either true or false (ignoring case)";
            public static final String DESCRIPTION_IS_INVALID = "Description must be between 1 and " + DESCRIPTION_MAX_LENGTH + " characters (inclusive)";
            public static final String TODO_NOT_FOUND = "Todo not found";
            public static final String TODO_EXISTS = "Todo already exists";
        }
    }
}
