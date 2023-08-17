package com.example.todoserver.model;

import com.example.todoserver.annotation.BatchUpdatable;
import com.example.todoserver.annotation.NullOrNotBlank;
import com.example.todoserver.configuration.ConstantValues;
import com.example.todoserver.exception.ParseException;
import com.example.todoserver.web.controller.validation.group.TodoValidationGroup;
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
import java.io.IOException;
import java.time.LocalDateTime;
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
    @NotBlank(message = ConstantValues.Todo.Error.TITLE_NEEDED, groups = {TodoValidationGroup.Create.class})
    @NullOrNotBlank(message = ConstantValues.Todo.Error.TITLE_NEEDED, groups = {TodoValidationGroup.Update.class})
    @Length(
            max = ConstantValues.Todo.TITLE_MAX_LENGTH,
            message = ConstantValues.Todo.Error.TITLE_IS_INVALID,
            groups = {TodoValidationGroup.Create.class, TodoValidationGroup.Update.class}
    )
    @BatchUpdatable
    @Column(name = "title", nullable = false)
    private String title;

    @JsonProperty(value = ConstantValues.Todo.Json.CREATED_AT, access = JsonProperty.Access.READ_ONLY)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @JsonProperty(value = ConstantValues.Todo.Json.UPDATED_AT, access = JsonProperty.Access.READ_ONLY)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @JsonProperty(value = ConstantValues.Todo.Json.SCHEDULED_AT)
    @NotNull(message = ConstantValues.Todo.Error.SCHEDULED_AT_NEEDED, groups = {TodoValidationGroup.Create.class})
    @FutureOrPresent(message = ConstantValues.Todo.Error.SCHEDULED_AT_IS_INVALID, groups = {TodoValidationGroup.Create.class, TodoValidationGroup.Update.class})
    @BatchUpdatable
    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @JsonProperty(value = ConstantValues.Todo.Json.COMPLETED)
    @JsonDeserialize(using = CompletedFieldDeserializer.class)
    @NotNull(message = ConstantValues.Todo.Error.COMPLETED_NEEDED, groups = {TodoValidationGroup.Create.class})
    @BatchUpdatable
    @Column(name = "completed", nullable = false)
    private Boolean completed;

    @JsonProperty(value = ConstantValues.Todo.Json.DESCRIPTION)
    @NotBlank(message = ConstantValues.Todo.Error.DESCRIPTION_NEEDED, groups = {TodoValidationGroup.Create.class})
    @NullOrNotBlank(message = ConstantValues.Todo.Error.DESCRIPTION_NEEDED, groups = {TodoValidationGroup.Update.class})
    @Length(
            max = ConstantValues.Todo.DESCRIPTION_MAX_LENGTH,
            message = ConstantValues.Todo.Error.DESCRIPTION_IS_INVALID,
            groups = {TodoValidationGroup.Create.class, TodoValidationGroup.Update.class}
    )
    @BatchUpdatable
    @Column(name = "description", length = ConstantValues.Todo.DESCRIPTION_MAX_LENGTH, nullable = false)
    private String description;

    @JsonProperty(value = ConstantValues.Todo.Json.USER, access = JsonProperty.Access.READ_ONLY)
    @ManyToOne
    @JoinColumn(name = "username", nullable = false)
    User user;

    private static class CompletedFieldDeserializer extends JsonDeserializer<Boolean> {

        @Override
        public Boolean deserialize(JsonParser p, DeserializationContext context) throws IOException, JacksonException {

            JsonNode node = p.getCodec().readTree(p);

            if (node.isBoolean()) {
                return node.booleanValue();
            }
            else if (node.isTextual()) {
                String value = node.textValue();

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
}
