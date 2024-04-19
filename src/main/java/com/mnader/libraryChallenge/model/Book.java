package com.mnader.libraryChallenge.model;

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
}
