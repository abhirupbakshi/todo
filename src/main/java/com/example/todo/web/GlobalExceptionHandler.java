package com.example.todo.web;

import com.example.todo.exception.AlreadyExistException;
import com.example.todo.exception.NotFoundException;
import com.example.todo.exception.ParseException;
import com.example.todo.exception.ConstraintValidationException;
import com.example.todo.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {

        ErrorResponse error = new ErrorResponse().setMessages(List.of(ex.getMessage()));
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        logger.warn("HttpMediaTypeNotSupportedException occurred: {}", ex.getMessage());

        return new ResponseEntity<>(error, headers, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {

        ErrorResponse error = new ErrorResponse().setMessages(List.of(ex.getMessage()));
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        logger.warn("HttpRequestMethodNotSupportedException occurred: {}", ex.getMessage());

        return new ResponseEntity<>(error, headers, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        ErrorResponse error = new ErrorResponse();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        if (ex.getRootCause() instanceof ParseException cause) {
            error.setMessages(List.of(cause.getMessage()));
            logger.warn("ParseException occurred: {}", ex.getMessage());
        }
        else {
            error.setMessages(List.of());
            logger.warn("{} occurred: {}", ex.getClass().getSimpleName(), ex.getMessage());
        }

        return new ResponseEntity<>(error, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {

        ErrorResponse error = new ErrorResponse();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        error.setMessages(List.of());

        logger.warn("MethodArgumentTypeMismatchException occurred: {}", ex.getMessage());

        return new ResponseEntity<>(error, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ConstraintValidationException ex) {

        ErrorResponse error = new ErrorResponse().setMessages(ex.getMessages());
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        logger.warn("ConstraintValidationException occurred: {}", ex.getMessages());

        return new ResponseEntity<>(error, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {

        ErrorResponse error = new ErrorResponse().setMessages(List.of(ex.getMessage()));
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        logger.warn("IllegalArgumentException occurred: {}", ex.getMessage());

        return new ResponseEntity<>(error, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistException(AlreadyExistException ex) {

        ErrorResponse error = new ErrorResponse().setMessages(List.of(ex.getMessage()));
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        logger.warn("AlreadyExistException occurred: {}", ex.getMessage());

        return new ResponseEntity<>(error, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {

        ErrorResponse error = new ErrorResponse().setMessages(List.of(ex.getMessage()));
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        logger.warn("NotFoundException occurred: {}", ex.getMessage());

        return new ResponseEntity<>(error, headers, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {

        logger.error("Exception occurred:", ex);

        ErrorResponse error = new ErrorResponse().setMessages(List.of());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
