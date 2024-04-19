package com.mnader.libraryChallenge.controller;

import com.mnader.libraryChallenge.controller.exceptions.BadRequestException;
import com.mnader.libraryChallenge.controller.exceptions.NotFoundException;
import com.mnader.libraryChallenge.controller.exceptions.OutOfRangeRatingException;
import com.mnader.libraryChallenge.model.Book;
import com.mnader.libraryChallenge.controller.DTO.BookRequestDTO;
import com.mnader.libraryChallenge.controller.DTO.BookResponseDTO;
import com.mnader.libraryChallenge.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/library")
public class LibraryController {
    BookService bookService;
    @Autowired
    public LibraryController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/book")
    public ResponseEntity<BookResponseDTO> addBook(
        @Valid @RequestBody BookRequestDTO bookRequestDTO
    ) throws BadRequestException {
        Book book = bookService.saveBook(bookRequestDTO.convertToEntity());

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new BookResponseDTO().buildResponseDtoFromEntity(book)
        );
    }

    @GetMapping("/book/{bookTitle}")
    public ResponseEntity<List<BookResponseDTO>> findBooksByTitle (
        @PathVariable String bookTitle
    ) {
        List<Book> bookList = bookService.findBooksByTitle(bookTitle);
        List<BookResponseDTO> bookResponseDTOList = bookList.stream().map(book ->
            new BookResponseDTO().buildResponseDtoFromEntity(book)
        ).toList();
        return ResponseEntity.status(HttpStatus.OK).body(bookResponseDTOList);
    }

    @DeleteMapping("/book/{bookID}")
    public ResponseEntity<String> deleteBookByID(
        @PathVariable Long bookID
    ) throws NotFoundException, BadRequestException {
        bookService.deleteBookByID(bookID);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/book/{bookID}")
    public ResponseEntity<BookResponseDTO> updateBookByID(
        @Valid @RequestBody BookRequestDTO bookRequestDTO,
        @PathVariable Long bookID
    ) throws NotFoundException {
        Book bookToUpdate = bookService.updateBookByID(bookID, bookRequestDTO.convertToEntity());

        return ResponseEntity.status(HttpStatus.OK).body(
            new BookResponseDTO().buildResponseDtoFromEntity(bookToUpdate)
        );
    }

    @PutMapping("/book/{bookID}/rate_book")
    public ResponseEntity<String> rateBookByID(
        @PathVariable Long bookID,
        @RequestParam Integer rating
    ) throws NotFoundException, OutOfRangeRatingException {
        bookService.rateBook((double) rating, bookID);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/book/{bookID}/add_copies")
    public ResponseEntity<String> addCopiesOfBookToStock(
        @PathVariable Long bookID,
        @RequestParam(required = false, defaultValue = "1") Integer quantity
    ) throws NotFoundException, BadRequestException {
        bookService.addCopiesOfBookToStock(quantity, bookID);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/book/{bookID}/delete_copies")
    public ResponseEntity<String> deleteCopiesOfBookFromStock(
        @PathVariable Long bookID,
        @RequestParam(required = false, defaultValue = "1") Integer quantity
    ) throws NotFoundException, BadRequestException {
        bookService.deleteCopiesOfBookFromStock(quantity, bookID);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/auth_token")
    public ResponseEntity<String> getTokenJwt(@AuthenticationPrincipal OidcUser principal) {
        return ResponseEntity.status(HttpStatus.OK).body(principal.getIdToken().getTokenValue());
    }
}
