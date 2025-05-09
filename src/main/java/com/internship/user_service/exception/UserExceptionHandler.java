package com.internship.user_service.exception;

import com.internship.user_service.dto.ExceptionResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@Slf4j
@ControllerAdvice
public class UserExceptionHandler {

    private static ResponseEntity<ExceptionResponse> handleUserDefinedException(Exception ex, HttpStatus httpStatus) {
        String errorMessage = ex.getMessage();
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .statusCode(httpStatus.value())
                .messages(List.of(errorMessage))
                .success(false)
                .build();

        return ResponseEntity.status(httpStatus.value()).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errorMessages = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        log.error("ConstraintViolationException occurred: {}", errorMessages);

        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .messages(errorMessages)
                .success(false)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
//        String errorMessage = "Request failed because of an internal problem. " +
//                              "Please contact support or your administrator. Error: " + ex.getMessage();
//        log.error("Internal server error occurred: {}", errorMessage);
//
//        ExceptionResponse errorResponse = ExceptionResponse.builder()
//                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .messages(List.of(errorMessage))
//                .success(false)
//                .build();
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        log.error("MethodArgumentNotValidException occurred: {}", errorMessages);

        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .messages(errorMessages)
                .success(false)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("UserNotFoundException occurred: {}", ex.getMessage());
        return handleUserDefinedException(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PictureNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handlePictureNotFoundException(PictureNotFoundException ex) {
        log.error("PictureNotFoundException occurred: {}", ex.getMessage());
        return handleUserDefinedException(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleUserAlreadyExistsException(AlreadyExistsException ex) {
        log.error("AlreadyExistsException occurred: {}", ex.getMessage());
        return handleUserDefinedException(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionResponse> handleConflictException(ConflictException ex) {
        log.error("ConflictException occurred: {}", ex.getMessage());
        return handleUserDefinedException(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidTimeFormatException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidTimeException(InvalidTimeFormatException ex) {
        log.error("InvalidTimeFormatException occurred: {}", ex.getMessage());
        return handleUserDefinedException(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserUnavailableException.class)
    public ResponseEntity<ExceptionResponse> handleUnavailableUserException(UserUnavailableException ex) {
        log.error("InvalidTimeFormatException occurred: {}", ex.getMessage());
        return handleUserDefinedException(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException occurred: {}", ex.getMessage());
        return handleUserDefinedException(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ExceptionResponse> handleServiceUnavailableException(ServiceUnavailableException ex) {
        log.error("ServiceUnavailableException occurred: {}", ex.getMessage());
        return handleUserDefinedException(ex, HttpStatus.SERVICE_UNAVAILABLE);
    }

}
