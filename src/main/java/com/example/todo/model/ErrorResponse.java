package com.example.todo.model;

import com.example.todo.configuration.ConstantValues;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class ErrorResponse {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConstantValues.DATE_TIME_FORMAT_PATTERN);

    @JsonProperty(value = "messages")
    private List<String> messages;

    @Setter(value = AccessLevel.NONE)
    @JsonProperty(value = "timestamp")
    private LocalDateTime timestamp = LocalDateTime.parse(formatter.format(LocalDateTime.now()));
}
