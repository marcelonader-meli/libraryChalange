package com.mnader.libraryChallenge.model;


import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    Book book = Book.builder()
        .title("title")
            .id(1L)
            .author("author")
            .isbn("isbn")
            .price(99.90)
            .ratingList(List.of(5.0,4.0,2.0,4.0))
            .numberOfRatings(4)
            .stock(10)
            .build();

    Book bookWithoutCopiesInStock = Book.builder()
        .title("title")
        .id(1L)
        .author("author")
        .isbn("isbn")
        .price(99.90)
        .ratingList(List.of(5.0,4.0,2.0,4.0))
        .numberOfRatings(4)
        .stock(0)
        .build();

    @Test
    public void calculateAverageRatingCorrectly() {
        book.calculateAverageRating();
        assertEquals(3.75, book.getAverageRating());
    }

    @Test
    public void thereAreCopiesInStockShouldReturnTrue() {
        assertTrue(book.thereAreCopiesInStock());
    }

    @Test
    public void thereAreCopiesInStockShouldReturnFalse() {
        assertFalse(bookWithoutCopiesInStock.thereAreCopiesInStock());
    }
}