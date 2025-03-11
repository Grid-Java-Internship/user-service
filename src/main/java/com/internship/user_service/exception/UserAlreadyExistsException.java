package com.internship.user_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserAlreadyExistsException extends RuntimeException{
    private final String message;
}
