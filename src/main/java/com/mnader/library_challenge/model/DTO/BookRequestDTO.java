package com.mnader.library_challenge.model.DTO;

import com.mnader.library_challenge.model.Book;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.mnader.library_challenge.utils.Constants.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDTO {
    @NotBlank(message = BOOK_VALIDATOR_TITLE_MANDATORY_MESSAGE)
    @Size(min = 3, max = 255, message = BOOK_VALIDATOR_TITLE_OUT_OF_THE_RANGE_OF_CHARACTERS_MESSAGE)
    private String title;
    private String author;
    private String isbn;
    @PositiveOrZero
    private Double price;
    @PositiveOrZero
    private Integer stock;

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
