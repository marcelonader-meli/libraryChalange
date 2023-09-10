package com.mnader.library_challenge.service;

import com.mnader.library_challenge.controller.exceptions.BadRequestException;
import com.mnader.library_challenge.controller.exceptions.NotFoundException;
import com.mnader.library_challenge.controller.exceptions.OutOfRangeRatingException;
import com.mnader.library_challenge.model.Book;
import com.mnader.library_challenge.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.mnader.library_challenge.utils.Constants.*;

@Service
public class BookService {

    BookRepository bookRepository;

    public static final Double BOOK_MIN_RATE = 1.0;
    public static final Double BOOK_MAX_RATE = 5.0;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book saveBook(Book book) {
        Book persistedBook = bookRepository.findByIsbn(book.getIsbn()).orElse(null);
        if (persistedBook != null) {
            throw new BadRequestException(String.format(BOOK_ISBN_ALREADY_REGISTERED_MESSAGE, book.getIsbn()));
        }
        return bookRepository.saveAndFlush(book);
    }

    public List<Book> findBooksByTitle(String bookTitle) {
        return bookRepository.findByTitle(bookTitle);
    }

    public void deleteBookByID(Long bookID) {
        Book book = bookRepository.findById(bookID).orElseThrow(
            () -> new NotFoundException(BOOK_NOT_FOUND_MESSAGE)
        );
        if (book.thereAreCopiesInStock()) {
            throw new BadRequestException(String.format(BOOK_WITH_COPIES_IN_STOCK_MESSAGE, bookID));
        } else {
            bookRepository.deleteById(bookID);
        }
    }

    public Book updateBookByID(Long bookID, Book book) {
        Book persistedBook = bookRepository.findById(bookID).orElseThrow(
            () -> new NotFoundException(BOOK_NOT_FOUND_MESSAGE)
        );
        persistedBook.setTitle(book.getTitle());
        persistedBook.setAuthor(book.getAuthor());
        persistedBook.setIsbn(book.getIsbn());
        persistedBook.setPrice(book.getPrice());
        persistedBook.calculateAverageRating();
        persistedBook.setStock(book.getStock());

        return bookRepository.saveAndFlush(persistedBook);
    }

    public void rateBook(Double rating, Long bookID) {
        Book persistedBook = bookRepository.findById(bookID).orElseThrow(
            () -> new NotFoundException(BOOK_NOT_FOUND_MESSAGE)
        );
        persistedBook.getRatingList().add(rating);
        persistedBook.setNumberOfRatings(persistedBook.getRatingList().size());
        persistedBook.calculateAverageRating();
        if (rating < BOOK_MIN_RATE || rating > BOOK_MAX_RATE){
            throw new OutOfRangeRatingException(BOOK_OUT_OF_RANGE_RATING_MESSAGE);
        }
        bookRepository.saveAndFlush(persistedBook);
    }

    public void addCopiesOfBookToStock(Integer quantityToBeAdded, Long bookID) {
        Book persistedBook = bookRepository.findById(bookID).orElseThrow(
            () -> new NotFoundException(BOOK_NOT_FOUND_MESSAGE)
        );
        if (quantityToBeAdded < 0){
            throw new BadRequestException(BOOK_UNABLE_NEGATIVE_QUANTITY_TO_STOCK_MESSAGE);
        }
        persistedBook.setStock(persistedBook.getStock()+quantityToBeAdded);
        bookRepository.saveAndFlush(persistedBook);
    }

    public void deleteCopiesOfBookFromStock(Integer quantityToBeDeleted, Long bookID) {
        Book persistedBook = bookRepository.findById(bookID).orElseThrow(
            () -> new NotFoundException(BOOK_NOT_FOUND_MESSAGE)
        );
        if (quantityToBeDeleted < 0){
            throw new BadRequestException(BOOK_UNABLE_NEGATIVE_QUANTITY_TO_STOCK_MESSAGE);
        }
        if (persistedBook.getStock() < quantityToBeDeleted){
            throw new BadRequestException(BOOK_INSUFFICIENT_QUANTITY_IN_STOCK_MESSAGE);
        }
        persistedBook.setStock(persistedBook.getStock()-quantityToBeDeleted);
        bookRepository.saveAndFlush(persistedBook);
    }
}
