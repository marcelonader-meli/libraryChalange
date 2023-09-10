package com.mnader.library_challenge.controller.exceptions.handler;

import com.mnader.library_challenge.controller.exceptions.BadRequestException;
import com.mnader.library_challenge.controller.exceptions.NotFoundException;
import com.mnader.library_challenge.controller.exceptions.OutOfRangeRatingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<String> handleException(NotFoundException exception) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(exception.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    private ResponseEntity<String> handleException(BadRequestException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<Map<String, List<String>>> handleException(MethodArgumentNotValidException exception) {
        List<String> errors = exception.getBindingResult().getFieldErrors()
            .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(getErrorsMap(errors));
    }

    @ExceptionHandler(OutOfRangeRatingException.class)
    private ResponseEntity<String> handleException(OutOfRangeRatingException exception) {
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
