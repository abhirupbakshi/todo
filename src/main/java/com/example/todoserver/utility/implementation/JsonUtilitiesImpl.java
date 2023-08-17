package com.example.todoserver.utility.implementation;

import com.example.todoserver.utility.JsonUtilities;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;

/**
 * An implementation of the {@link JsonUtilities} interface.
 */
@Component
public class JsonUtilitiesImpl implements JsonUtilities {

    /**
     * A helper method for the {@link #toClassFields(String, Class)} method.
     */
    private String toClassFields(String[] json, Class<?> clazz, int i) {

        if (i >= json.length) return null;

        String classFields = "";

        for (Field field : clazz.getDeclaredFields()) {

            String jsonField = field.getAnnotation(JsonProperty.class).value();

            if (jsonField != null && jsonField.equals(json[i])) {

                classFields += field.getName();
                String temp = toClassFields(json, field.getType(), i + 1);

                if (temp != null) classFields += "." + temp;

                break;
            }
        }

        return classFields.isBlank() || classFields.split("\\.").length != (json.length - i) ? null : classFields;
    }

    public String toClassFields(String json, Class<?> clazz) {
        return toClassFields(json.split("\\."), clazz, 0);
    }
}
