package com.mnader.library_challenge.utils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Constants {
    public static final String BOOK_NOT_FOUND_MESSAGE = "BookID not found";
    public static final String BOOK_ISBN_ALREADY_REGISTERED_MESSAGE = "Book with ISBN %s is already registered in the system";
    public static final String BOOK_WITH_COPIES_IN_STOCK_MESSAGE = "Unable to delete book ID %s. There are copies of the book in stock";
    public static final String BOOK_OUT_OF_RANGE_RATING_MESSAGE = "Rating outside the allowed range of 1 to 5";
    public static final String BOOK_UNABLE_NEGATIVE_QUANTITY_TO_STOCK_MESSAGE = "Unable to add ou delete negative copy quantity to stock";
    public static final String BOOK_INSUFFICIENT_QUANTITY_IN_STOCK_MESSAGE = "Insufficient quantity in stock";
    public static final String BOOK_VALIDATOR_TITLE_MANDATORY_MESSAGE = "Title is mandatory";
    public static final String BOOK_VALIDATOR_TITLE_OUT_OF_THE_RANGE_OF_CHARACTERS_MESSAGE = "The title must be between 3 and 255 characters";
}
