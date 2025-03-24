package com.internship.user_service.exception;

public class UserUnavailableException extends RuntimeException {
    public UserUnavailableException(String message) {
        super(message);
    }
}
