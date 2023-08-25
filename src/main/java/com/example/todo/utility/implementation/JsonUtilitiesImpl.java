package com.example.todo.utility.implementation;

import com.example.todo.utility.JsonUtilities;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * An implementation of the {@link JsonUtilities} interface.
 */
@Component
public class JsonUtilitiesImpl implements JsonUtilities {

    private final Logger logger = LoggerFactory.getLogger(JsonUtilitiesImpl.class);

    /**
     * A helper method for the {@link #toClassFields(String, Class)} method.
     */
    private String toClassFields(String[] json, Class<?> clazz, int i) {

        logger.debug("Parameters:: json: {}, clazz: {} and i: {}", Arrays.toString(json), clazz, i);

        if (i >= json.length) return null;
        logger.debug("json[{}] = {}", i, json[i]);

        String classFields = "";

        for (Field field : clazz.getDeclaredFields()) {

            String jsonField = field.getAnnotation(JsonProperty.class).value();
            logger.debug("For class: {} with json field name: {} and java field name: {}", clazz, jsonField, field.getName());

            if (jsonField != null && jsonField.equals(json[i])) {

                classFields += field.getName();
                String temp = toClassFields(json, field.getType(), i + 1);
                logger.debug("Converted json to class field names from index: {} to the end = {}", i + 1, temp);

                if (temp != null) classFields += "." + temp;

                break;
            }
        }

        String classField = classFields.isBlank() || classFields.split("\\.").length != (json.length - i) ? null : classFields;
        logger.debug("Returning converted json to class field names from index: {} to the end = {}", i, classField);

        return classField;
    }

    public String toClassFields(String json, Class<?> clazz) {

        logger.debug("Parameters:: json: {}, clazz: {}", json, clazz);

        if (json == null) throw new RuntimeException("Json must not be null");
        logger.debug("Not null check passed for json");

        if (clazz == null) throw new RuntimeException("Class must not be null");
        logger.debug("Not null check passed for clazz");

        String classFields = toClassFields(json.split("\\."), clazz, 0);
        logger.info("Conversion from json field names: {} to class field names: {} for class: {} is done", json, classFields, clazz);

        return classFields;
    }
}
