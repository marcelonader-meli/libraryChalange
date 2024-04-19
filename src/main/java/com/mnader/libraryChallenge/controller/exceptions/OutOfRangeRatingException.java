package com.mnader.libraryChallenge.controller.exceptions;

public class OutOfRangeRatingException extends RuntimeException {

    public OutOfRangeRatingException(String message) {
        super(message);
    }
}
