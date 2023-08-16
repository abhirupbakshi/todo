package com.example.server.service.implementation;

import com.example.server.annotation.BatchUpdatable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * This generic class contains the common operations for all the models.
 * @param <T>
 */
abstract class AbstractModelServiceImpl<T> {

    /**
     * This method takes a current object and an updated object and updates the fields of the current object
     * that has been annotated with {@link BatchUpdatable} annotation. If any of the fields is null
     * in the provided updated object, then it is not copied to the current object.
     * @param current The current object
     * @param updated The updated object
     * @return True if any of the fields are updated, false otherwise
     */
    boolean batchUpdate(T current, T updated) {

        boolean isUpdated = false;

        for (Field field : updated.getClass().getDeclaredFields()) {

            if (field.getDeclaredAnnotation(BatchUpdatable.class) != null) {

                try {

                    String pascalCasedField = Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
                    Object returned = updated.getClass().getMethod("get" + pascalCasedField).invoke(updated);

                    if (returned != null) {
                        current.getClass().getMethod("set" + pascalCasedField, field.getType()).invoke(current, returned);
                        isUpdated = true;
                    }
                }
                catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return isUpdated;
    }
}
