package com.example.todo.utility;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An interface that provides helper methods for doing JSON related tasks.
 */
public interface JsonUtilities {

    /**
     * Converts a dot separated Json field name to its respective dot separated variable field name. The mapping of Json field
     * name to Java's variable field name is done by {@link JsonProperty} annotation. For example, if there are two class:
     * <br/>
     * <br/>
     * <pre>
     * class User {
     *      &#64;JsonProperty("user-name")
     *      private String username;
     *      &#64;JsonProperty("address")
     *      private Address address;
     * }
     *
     * class Address {
     *      &#64;JsonProperty("city-name")
     *      private String city;
     *      &#64;JsonProperty("state")
     *      private String state;
     *      &#64;JsonProperty("country")
     *      private String country;
     * }
     * </pre>
     * <br/>
     * <br/>
     * Then the following are equivalent:
     * <ul>
     *     <li>For class User: "user-name" = "username"</li>
     *     <li>For class User: "user-name.address" = "username.address"</li>
     *     <li>For class User: "user-name.address.city-name" = "username.address.city"</li>
     * </ul>
     * @param json The JSON field names.
     * @param clazz The class to search into.
     * @return The Converted field names or null if it could not be mapped.
     */
    String toClassFields(String json, Class<?> clazz);
}