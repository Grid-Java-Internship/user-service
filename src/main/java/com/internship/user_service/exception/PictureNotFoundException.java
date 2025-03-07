package com.internship.user_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PictureNotFoundException extends RuntimeException{
    private final String message;
}
