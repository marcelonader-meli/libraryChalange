package com.mnader.library_challenge.model;

import com.mnader.library_challenge.model.DTO.BookResponseDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Double price;
    @Setter(AccessLevel.PRIVATE)
    private Double averageRating = 0.0;
    private Integer stock;
    @ElementCollection
    @Setter(AccessLevel.PRIVATE)
    private List<Double> ratingList = new ArrayList<>();
    private Integer numberOfRatings = 0;

    public void calculateAverageRating() {
        this.averageRating = this.ratingList.stream().reduce(0.0, Double::sum) / this.numberOfRatings;
    }
    public Boolean thereAreCopiesInStock(){
        return this.stock > 0;
    }

    public BookResponseDTO convertToResponseDTO() {
        return BookResponseDTO.builder()
            .id(this.id)
            .title(this.title)
            .author(this.author)
            .isbn(this.isbn)
            .price(this.price)
            .averageRating(this.averageRating)
            .numberOfRatings(this.numberOfRatings)
            .stock(this.stock)
            .build();
    }
}
