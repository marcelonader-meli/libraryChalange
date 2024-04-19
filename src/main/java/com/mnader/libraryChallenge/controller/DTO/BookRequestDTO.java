package com.mnader.libraryChallenge.controller.DTO;

import com.mnader.libraryChallenge.model.Book;
import jakarta.validation.constraints.*;
import static com.mnader.libraryChallenge.utils.Constants.*;

public record BookRequestDTO(
    @NotBlank(message = BOOK_VALIDATOR_TITLE_MANDATORY_MESSAGE)
    @Size(min = 3, max = 255, message = BOOK_VALIDATOR_TITLE_OUT_OF_THE_RANGE_OF_CHARACTERS_MESSAGE)
    String title,
    String author,
    String isbn,
    @PositiveOrZero Double price,
    @PositiveOrZero Integer stock
) {
    public Book convertToEntity() {
        return Book.builder()
            .title(this.title)
            .author(this.author)
            .isbn(this.isbn)
            .price(this.price)
            .stock(this.stock)
            .build();
    }
}
