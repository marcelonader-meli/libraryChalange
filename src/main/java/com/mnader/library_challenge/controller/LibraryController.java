package com.mnader.library_challenge.controller;

import com.mnader.library_challenge.controller.exceptions.BadRequestException;
import com.mnader.library_challenge.controller.exceptions.NotFoundException;
import com.mnader.library_challenge.controller.exceptions.OutOfRangeRatingException;
import com.mnader.library_challenge.model.Book;
import com.mnader.library_challenge.model.DTO.BookRequestDTO;
import com.mnader.library_challenge.model.DTO.BookResponseDTO;
import com.mnader.library_challenge.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        Book bookEntity = bookRequestDTO.convertToEntity();
        Book book = bookService.saveBook(bookEntity);
        BookResponseDTO bookResponseDTO = book.convertToResponseDTO();
        return ResponseEntity.status(HttpStatus.CREATED).body(bookResponseDTO);
    }

    @GetMapping("/book/{bookTitle}")
    public ResponseEntity<List<BookResponseDTO>> findBooksByTitle (
        @PathVariable String bookTitle
    ) {
        List<Book> bookList = bookService.findBooksByTitle(bookTitle);
        List<BookResponseDTO> bookResponseDTOList = bookList.stream().map(Book::convertToResponseDTO).toList();
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
        Book book = bookRequestDTO.convertToEntity();
        Book bookToUpdate = bookService.updateBookByID(bookID, book);
        return ResponseEntity.status(HttpStatus.OK).body(bookToUpdate.convertToResponseDTO());
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
}
