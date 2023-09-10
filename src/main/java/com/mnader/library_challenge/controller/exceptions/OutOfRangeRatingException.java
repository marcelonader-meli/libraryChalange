package com.mnader.library_challenge.controller.exceptions;

public class OutOfRangeRatingException extends RuntimeException {

    public OutOfRangeRatingException(String message) {
        super(message);
    }
}
