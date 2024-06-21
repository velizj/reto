package com.example.demo.controller;

import com.example.demo.exception.InvalidUrlException;
import com.example.demo.exception.UrlNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class UrlShortenerControllerAdvice extends ResponseEntityExceptionHandler {

  private static final String TIMESTAMP = "timestamp";
  private static final String STATUS = "status";
  private static final String ERROR = "error";
  private static final String MESSAGE = "message";

  @ExceptionHandler(InvalidUrlException.class)
  public ResponseEntity<Object> handleInvalidUrlException(
      InvalidUrlException ex, WebRequest request) {
    Map<String, Object> body = new HashMap<>();
    body.put(TIMESTAMP, LocalDateTime.now());
    body.put(STATUS, HttpStatus.BAD_REQUEST.value());
    body.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
    body.put(MESSAGE, ex.getMessage());
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UrlNotFoundException.class)
  public ResponseEntity<Object> handleUrlNotFoundException(
      UrlNotFoundException ex, WebRequest request) {
    Map<String, Object> body = new HashMap<>();
    body.put(TIMESTAMP, LocalDateTime.now());
    body.put(STATUS, HttpStatus.NOT_FOUND.value());
    body.put(ERROR, HttpStatus.NOT_FOUND.getReasonPhrase());
    body.put(MESSAGE, ex.getMessage());
    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
    Map<String, Object> body = new HashMap<>();
    body.put(TIMESTAMP, LocalDateTime.now());
    body.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
    body.put(ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    body.put(MESSAGE, ex.getMessage());
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      org.springframework.http.HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    Map<String, Object> body = new HashMap<>();
    body.put(TIMESTAMP, LocalDateTime.now());
    body.put(STATUS, status.value());
    body.put(ERROR, "Validation Failed");

    String errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.joining(", "));
    body.put(MESSAGE, errors);
    body.put("path", request.getDescription(false).replace("uri=", ""));

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }
}
