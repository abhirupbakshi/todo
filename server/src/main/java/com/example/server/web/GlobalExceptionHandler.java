package com.example.server.web;

import com.example.server.exception.AlreadyExistException;
import com.example.server.exception.NotFoundException;
import com.example.server.exception.ParseException;
import com.example.server.model.ErrorResponseModel;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseModel> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        ErrorResponseModel error = new ErrorResponseModel();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        if (ex.getRootCause() instanceof ParseException cause) {
            error.setMessages(List.of(cause.getMessage()));
        }
        else {
            error.setMessages(List.of());
        }

        return new ResponseEntity<>(error, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseModel> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        List<String> body = ex.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        ErrorResponseModel error = new ErrorResponseModel().setMessages(body);
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(error, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseModel> handleIllegalArgumentException(IllegalArgumentException ex) {

        ErrorResponseModel error = new ErrorResponseModel().setMessages(List.of(ex.getMessage()));
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(error, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<ErrorResponseModel> handleAlreadyExistException(AlreadyExistException ex) {

        ErrorResponseModel error = new ErrorResponseModel().setMessages(List.of(ex.getMessage()));
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(error, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseModel> handleNotFoundException(NotFoundException ex) {

        ErrorResponseModel error = new ErrorResponseModel().setMessages(List.of(ex.getMessage()));
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(error, headers, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseModel> handleException(Exception ex) {

        ErrorResponseModel error = new ErrorResponseModel().setMessages(List.of());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
