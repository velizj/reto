package com.example.demo.exception;

public class UrlShortenerException extends RuntimeException {
    public UrlShortenerException(String message) {
        super(message);
    }
}