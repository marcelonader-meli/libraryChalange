package com.mnader.library_challenge.controller.DTO;

import com.mnader.library_challenge.model.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDTO {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Double price;
    private Double averageRating;
    private Integer numberOfRatings;
    private Integer stock;

    public BookResponseDTO buildResponseDtoFromEntity(Book bookEntity) {
        return BookResponseDTO.builder()
            .id(bookEntity.getId())
            .title(bookEntity.getTitle())
            .author(bookEntity.getAuthor())
            .isbn(bookEntity.getIsbn())
            .price(bookEntity.getPrice())
            .averageRating(bookEntity.getAverageRating())
            .numberOfRatings(bookEntity.getNumberOfRatings())
            .stock(bookEntity.getStock())
            .build();
    }
}
