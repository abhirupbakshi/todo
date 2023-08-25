package com.example.todo.service.implementation;

import com.example.todo.annotation.BatchUpdatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * This generic class contains the common operations for all the models.
 * @param <T>
 */
abstract class AbstractModelServiceImpl<T> {

    private final Logger logger = LoggerFactory.getLogger(AbstractModelServiceImpl.class);

    /**
     * This method takes a current object and an updated object and updates the fields of the current object
     * that has been annotated with {@link BatchUpdatable} annotation. If any of the fields is null
     * in the provided updated object, then it is not copied to the current object.
     * @param current The current object
     * @param updated The updated object
     * @return True if any of the fields are updated, false otherwise
     */
    boolean batchUpdate(T current, T updated) {

        logger.debug("Parameters:: current: {}, updated: {}", current, updated);

        if (current == null) throw new RuntimeException("current object cannot be null");
        logger.debug("Not null check passed for current object");

        if (updated == null) throw new RuntimeException("updated object cannot be null");
        logger.debug("Not null check passed for updated object");

        boolean isUpdated = false;

        for (Field field : updated.getClass().getDeclaredFields()) {

            logger.debug("Field: {} of updated object class: {}", field.getName(), updated.getClass().getName());

            if (field.getDeclaredAnnotation(BatchUpdatable.class) != null) {

                logger.debug("Field: {} is annotated with {} annotation", field.getName(), BatchUpdatable.class.getName());

                try {

                    String pascalCasedField = Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
                    logger.debug("Converted pascal cased field name: {}", pascalCasedField);

                    Object returned = updated.getClass().getMethod("get" + pascalCasedField).invoke(updated);
                    logger.debug("Value of the field {} is {}", field.getName(), returned);

                    if (returned != null) {
                        current.getClass().getMethod("set" + pascalCasedField, field.getType()).invoke(current, returned);
                        logger.debug("Copied field {} value from updated object to current object", field.getName());

                        isUpdated = true;
                    }
                }
                catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        logger.debug("Value of isUpdated: {}", isUpdated);

        return isUpdated;
    }
}
