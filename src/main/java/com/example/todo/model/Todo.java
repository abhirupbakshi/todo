package com.example.todo.model;

import com.example.todo.annotation.BatchUpdatable;
import com.example.todo.annotation.NullOrNotBlank;
import com.example.todo.configuration.ConstantValues;
import com.example.todo.exception.ParseException;
import com.example.todo.model.validation.ValidationGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Entity
@Table(name = "todos")
@NoArgsConstructor(force = true)
@Getter
@Setter
@Accessors(chain = true)
public class Todo {

    @JsonProperty(value = ConstantValues.Todo.Json.ID, access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @JsonProperty(value = ConstantValues.Todo.Json.TITLE)
    @NotBlank(message = ConstantValues.Todo.Error.TITLE_NEEDED, groups = {ValidationGroup.Todo.Create.class})
    @NullOrNotBlank(message = ConstantValues.Todo.Error.TITLE_NEEDED, groups = {ValidationGroup.Todo.Update.class})
    @Length(
            max = ConstantValues.Todo.TITLE_MAX_LENGTH,
            message = ConstantValues.Todo.Error.TITLE_IS_INVALID,
            groups = {ValidationGroup.Todo.Create.class, ValidationGroup.Todo.Update.class}
    )
    @BatchUpdatable
    @Column(name = "title", length = ConstantValues.Todo.TITLE_MAX_LENGTH, nullable = false)
    private String title;

    @JsonProperty(value = ConstantValues.Todo.Json.CREATED_AT, access = JsonProperty.Access.READ_ONLY)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @JsonProperty(value = ConstantValues.Todo.Json.UPDATED_AT, access = JsonProperty.Access.READ_ONLY)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @JsonProperty(value = ConstantValues.Todo.Json.SCHEDULED_AT)
    @JsonDeserialize(using = ScheduledAtFieldDeserializer.class)
    @NotNull(message = ConstantValues.Todo.Error.SCHEDULED_AT_NEEDED, groups = {ValidationGroup.Todo.Create.class})
    @FutureOrPresent(message = ConstantValues.Todo.Error.SCHEDULED_AT_SHOULD_BE_PRESENT_FUTURE, groups = {ValidationGroup.Todo.Create.class, ValidationGroup.Todo.Update.class})
    @BatchUpdatable
    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @JsonProperty(value = ConstantValues.Todo.Json.COMPLETED)
    @JsonDeserialize(using = CompletedFieldDeserializer.class)
    @NotNull(message = ConstantValues.Todo.Error.COMPLETED_NEEDED, groups = {ValidationGroup.Todo.Create.class})
    @BatchUpdatable
    @Column(name = "completed", nullable = false)
    private Boolean completed;

    @JsonProperty(value = ConstantValues.Todo.Json.DESCRIPTION)
    @NotBlank(message = ConstantValues.Todo.Error.DESCRIPTION_NEEDED, groups = {ValidationGroup.Todo.Create.class})
    @NullOrNotBlank(message = ConstantValues.Todo.Error.DESCRIPTION_NEEDED, groups = {ValidationGroup.Todo.Update.class})
    @Length(
            max = ConstantValues.Todo.DESCRIPTION_MAX_LENGTH,
            message = ConstantValues.Todo.Error.DESCRIPTION_IS_INVALID,
            groups = {ValidationGroup.Todo.Create.class, ValidationGroup.Todo.Update.class}
    )
    @BatchUpdatable
    @Column(name = "description", length = ConstantValues.Todo.DESCRIPTION_MAX_LENGTH, nullable = false)
    private String description;

    @JsonProperty(value = ConstantValues.Todo.Json.USER, access = JsonProperty.Access.READ_ONLY)
    @ManyToOne
    @JoinColumn(name = "username", nullable = false)
    User user;

    private static class CompletedFieldDeserializer extends JsonDeserializer<Boolean> {

        private final Logger logger = LoggerFactory.getLogger(CompletedFieldDeserializer.class);

        @Override
        public Boolean deserialize(JsonParser p, DeserializationContext context) throws IOException, JacksonException {

            logger.debug("Parameters:: p: {}, context: {}", p, context);

            JsonNode node = p.getCodec().readTree(p);
            logger.debug("Node: {}", node);

            if (node.isBoolean()) {
                logger.debug("Node is boolean with value: {}", node.booleanValue());
                return node.booleanValue();
            }
            else if (node.isTextual()) {

                String value = node.textValue();
                logger.debug("Node is textual with value: {}", value);

                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
                    return Boolean.parseBoolean(value);
                else
                    throw new ParseException(ConstantValues.Todo.Error.COMPLETED_IS_INVALID);
            }
            else {
                throw new IOException(ConstantValues.Todo.Error.COMPLETED_IS_INVALID);
            }
        }
    }

    private static class ScheduledAtFieldDeserializer extends JsonDeserializer<LocalDateTime> {

        private final Logger logger = LoggerFactory.getLogger(ScheduledAtFieldDeserializer.class);

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext context) throws IOException, JacksonException {

            logger.debug("Parameters:: p: {}, context: {}", p, context);

            JsonNode node = p.getCodec().readTree(p);
            logger.debug("Node: {}", node);

            if (node.isTextual()) {

                String value = node.textValue();
                logger.debug("Node is textual with value: {}", value);

                try {
                    LocalDateTime parsed = LocalDateTime.parse(value);
                    logger.debug("Parsed value: {}", parsed);

                    return parsed;
                }
                catch (DateTimeParseException e) {
                    throw new ParseException(ConstantValues.Todo.Error.Scheduled_At_Field_IS_INVALID);
                }
            }
            else {
                throw new IOException(ConstantValues.Todo.Error.COMPLETED_IS_INVALID);
            }
        }
    }
}
