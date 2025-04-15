package com.internship.user_service.exception;

public class InvalidTimeFormatException extends RuntimeException {
    public InvalidTimeFormatException(String message) {
        super(message);
    }
}
