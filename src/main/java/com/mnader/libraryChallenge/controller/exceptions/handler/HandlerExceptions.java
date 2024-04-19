package com.mnader.libraryChallenge.controller.exceptions.handler;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.mnader.libraryChallenge.controller.exceptions.BadRequestException;
import com.mnader.libraryChallenge.controller.exceptions.NotFoundException;
import com.mnader.libraryChallenge.controller.exceptions.OutOfRangeRatingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mnader.libraryChallenge.utils.Constants.LOGIN_NOT_FOUND_MESSAGE;

@ControllerAdvice
public class HandlerExceptions {
    @ExceptionHandler(Exception.class)
    private ResponseEntity<ErrorResponse> handleException(Exception exception) {

        return ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .body(
                ErrorResponse.builder(exception, HttpStatus.BAD_GATEWAY, exception.getLocalizedMessage()).build()
            );
    }

    @ExceptionHandler(RuntimeException.class)
    private ResponseEntity<String> handleRuntimeException(Exception exception) {

        return ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .body(exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<String> handleNotFoundException(NotFoundException exception) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(exception.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    private ResponseEntity<String> handleBadRequestException(BadRequestException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<Map<String, List<String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<String> errors = exception.getBindingResult().getFieldErrors()
            .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(getErrorsMap(errors));
    }

    @ExceptionHandler(OutOfRangeRatingException.class)
    private ResponseEntity<String> handleOutOfRangeRatingException(OutOfRangeRatingException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(exception.getMessage());
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    private ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(exception.getMessage());
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    private ResponseEntity<String> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(LOGIN_NOT_FOUND_MESSAGE);
    }

    @ExceptionHandler(JWTVerificationException.class)
    private ResponseEntity<String> handleJWTVerificationException(JWTVerificationException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(exception.getMessage());
    }

    @ExceptionHandler(JWTCreationException.class)
    private ResponseEntity<String> handleJWTCreationException(JWTCreationException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(exception.getMessage());
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }
}
