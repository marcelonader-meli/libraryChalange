package com.mnader.libraryChallenge.service;

import com.mnader.libraryChallenge.controller.exceptions.BadRequestException;
import com.mnader.libraryChallenge.controller.exceptions.NotFoundException;
import com.mnader.libraryChallenge.controller.exceptions.OutOfRangeRatingException;
import com.mnader.libraryChallenge.model.Book;
import com.mnader.libraryChallenge.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mnader.libraryChallenge.utils.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    
    @InjectMocks
    private BookService bookService;

    private Book bookCorrect;
    private Book bookPersistedWithCopiesInStock;
    private Book bookCorrectPersisted;
    private Book bookCorrectUpdate;
    private Book bookCorrectUpdatePersisted;
    private Book bookWithExistentIsbn;
    private Book bookCorrectPersistedWithSameTitleButDifferentIsbn;
    private Book bookCorrectPersistedWithoutCopiesInStock;

    @BeforeEach
    public void setupBefore() {
        MockitoAnnotations.openMocks(this);

        this.bookCorrect = Book.builder()
            .title("title")
            .author("author")
            .isbn("isbn")
            .price(99.90)
            .stock(10)
            .build();

        this.bookPersistedWithCopiesInStock = Book.builder()
            .id(1L)
            .title("title")
            .author("author")
            .isbn("isbnDifferent")
            .price(99.90)
            .numberOfRatings(0)
            .averageRating(0.0)
            .stock(10)
            .build();

        this.bookCorrectPersistedWithSameTitleButDifferentIsbn = Book.builder()
            .id(1L)
            .title("title")
            .author("author")
            .isbn("isbnDifferent")
            .price(99.90)
            .numberOfRatings(0)
            .averageRating(0.0)
            .stock(10)
            .build();

        this.bookCorrectPersisted = Book.builder()
            .id(1L)
            .title("title")
            .author("author")
            .isbn("isbn")
            .price(99.90)
            .numberOfRatings(0)
            .averageRating(0.0)
            .ratingList(new ArrayList<>())
            .stock(10)
            .build();

        this.bookCorrectUpdate = Book.builder()
            .title("title")
            .author("author")
            .isbn("isbn")
            .price(89.90)
            .stock(10)
            .build();

        this.bookCorrectUpdatePersisted = Book.builder()
            .id(2L)
            .title("title")
            .author("author")
            .isbn("isbn")
            .price(89.90)
            .numberOfRatings(0)
            .averageRating(0.0)
            .ratingList(new ArrayList<>())
            .stock(10)
            .build();

        this.bookWithExistentIsbn = Book.builder()
            .title("title_bookWithExistentIsbn")
            .author("author")
            .isbn("isbn")
            .price(25.0)
            .stock(5)
            .build();

        this.bookCorrectPersistedWithoutCopiesInStock = Book.builder()
            .id(1L)
            .title("title")
            .author("author")
            .isbn("isbn")
            .price(99.90)
            .numberOfRatings(0)
            .averageRating(0.0)
            .ratingList(new ArrayList<>())
            .stock(0)
            .build();
    }

    @Nested
    class SaveBookFunctionTests {
        @Test
        public void whenTryingToAddABookWithAnAlreadyExistingIsbnItShouldThrowAnBadRequestException() {
            BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
                when(bookRepository.saveAndFlush(bookCorrect)).thenReturn(bookCorrectPersisted);
                when(bookRepository.findByIsbn(bookCorrect.getIsbn())).thenReturn(Optional.empty());
                bookService.saveBook(bookCorrect);
                when(bookRepository.findByIsbn(bookWithExistentIsbn.getIsbn())).thenReturn(Optional.of(bookCorrectPersisted));
                bookService.saveBook(bookWithExistentIsbn);
            });
            assertEquals(String.format(BOOK_ISBN_ALREADY_REGISTERED_MESSAGE, bookWithExistentIsbn.getIsbn()), thrown.getMessage());
        }

        @Test
        public void whenTryingToAddABookShouldReturnABookPersistedCorrectly() throws BadRequestException {
                when(bookRepository.saveAndFlush(bookCorrect)).thenReturn(bookCorrectPersisted);
                when(bookRepository.findByIsbn(bookCorrect.getIsbn())).thenReturn(Optional.empty());
                Book bookPersisted = bookService.saveBook(bookCorrect);
                assertEquals(bookCorrect.getTitle(), bookPersisted.getTitle());
        }
    }

    @Nested
    class FindBooksByTitleFunctionTests {
        @Test
        public void whenSearchingForBooksByTitleItShouldReturnAListOfAllBooksWithThatTitle(){
            String title = "title";
            List<Book> expectedBookList = List.of(bookCorrectPersisted, bookCorrectPersistedWithSameTitleButDifferentIsbn);
            when(bookRepository.findByTitle(title)).thenReturn(expectedBookList);
            List<Book> bookList = bookService.findBooksByTitle(title);
            assertEquals(expectedBookList, bookList);
        }
    }

    @Nested
    class DeleteBookByIDFunctionTests {
        @Test
        public void whenTryingToDeleteABookThatDoesNotExistInTheDatabaseItShouldThrowAnNotFoundException(){
            NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
                when(bookRepository.findById(bookCorrect.getId())).thenReturn(Optional.empty());
                bookService.deleteBookByID(bookCorrect.getId());
            });
            assertEquals(BOOK_NOT_FOUND_MESSAGE, thrown.getMessage());
        }

        @Test
        public void whenTryingToDeleteABookThatStillHasCopiesInStockItShouldThrowABadRequestException(){
            BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
                when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(bookPersistedWithCopiesInStock));
                bookService.deleteBookByID(1L);
            });
            assertEquals(String.format(BOOK_WITH_COPIES_IN_STOCK_MESSAGE, bookPersistedWithCopiesInStock.getId()), thrown.getMessage());
        }

        @Test
        public void whenTryingToDeleteABookShouldDeleteCorrectly() throws BadRequestException, NotFoundException {
            when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(bookCorrectPersistedWithoutCopiesInStock));
            bookService.deleteBookByID(1L);
            verify(bookRepository).deleteById(1L);
        }
    }

    @Nested
    class UpdateBookByIDFunctionTests {
        @Test
        public void whenTryingToUpdateABookThatDoesNotExistInTheDatabaseItShouldThrowAnNotFoundException(){
            NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
                when(bookRepository.findById(bookCorrect.getId())).thenReturn(Optional.empty());
                bookService.updateBookByID(bookCorrect.getId(), bookCorrect);
            });
            assertEquals(BOOK_NOT_FOUND_MESSAGE, thrown.getMessage());
        }

        @Test
        public void whenTryingToUpdateABookShouldReturnABookUpdatedCorrectly() throws NotFoundException {
            when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(bookCorrectPersisted));
            when(bookRepository.saveAndFlush(bookCorrectPersisted)).thenReturn(bookCorrectUpdatePersisted);
            Book bookUpdated = bookService.updateBookByID(1L, bookCorrectUpdate);
            assertEquals(bookCorrectUpdatePersisted.getPrice(), bookUpdated.getPrice());
        }
    }

    @Nested
    class RateBookFunctionTests {
        @Test
        public void whenAddingMultipleRatingsTheyMustBeAveragedCorrectly() throws OutOfRangeRatingException, NotFoundException {
            when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(bookCorrectPersisted));
            assertEquals(0.0, bookCorrectPersisted.getAverageRating());
            assertEquals(0, bookCorrectPersisted.getNumberOfRatings());
            Double rating3 = 3.0;
            bookService.rateBook(rating3, 1L);
            assertEquals(3.0, bookCorrectPersisted.getAverageRating());
            assertEquals(1, bookCorrectPersisted.getNumberOfRatings());
            Double rating2 = 2.0;
            bookService.rateBook(rating2, 1L);
            assertEquals(2.5, bookCorrectPersisted.getAverageRating());
            assertEquals(2, bookCorrectPersisted.getNumberOfRatings());
        }

        @Test
        public void whenTryingToRateABookThatDoesNotExistInTheDatabaseItShouldThrowAnNotFoundException(){
            NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
                when(bookRepository.findById(bookCorrect.getId())).thenReturn(Optional.empty());
                Double rating3 = 3.0;
                bookService.rateBook(rating3, bookCorrect.getId());
            });
            assertEquals(BOOK_NOT_FOUND_MESSAGE, thrown.getMessage());
        }

        @Test
        public void whenTryingToRateABookWithAGradeAboveTheAllowedRangeAnOutOfRangeRatingExceptionShouldBeThrown(){
            OutOfRangeRatingException thrown = assertThrows(OutOfRangeRatingException.class, () -> {
                when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(bookCorrectPersisted));
                bookService.rateBook(6.0, 1L);
            });
            assertEquals(BOOK_OUT_OF_RANGE_RATING_MESSAGE, thrown.getMessage());
        }

        @Test
        public void whenTryingToRateABookWithAGradeBelowTheAllowedRangeAnOutOfRangeRatingExceptionShouldBeThrown(){
            OutOfRangeRatingException thrown = assertThrows(OutOfRangeRatingException.class, () -> {
                when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(bookCorrectPersisted));
                bookService.rateBook(0.0, 1L);
            });
            assertEquals(BOOK_OUT_OF_RANGE_RATING_MESSAGE, thrown.getMessage());
        }
    }

    @Nested
    class AddCopiesOfBookToStockFunctionTests {
        @Test
        public void whenTryingToAddANegativeAmountOfCopiesToTheStockABadRequestExceptionShouldBeThrown(){
            BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
                when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(bookCorrectPersisted));
                bookService.addCopiesOfBookToStock(-1, 1L);
            });
            assertEquals(BOOK_UNABLE_NEGATIVE_QUANTITY_TO_STOCK_MESSAGE, thrown.getMessage());
        }

        @Test
        public void whenTryingToAddAnAmountOfCopiesToTheStockOfABookThatDoesNotExistInTheDatabaseItShouldThrowAnNotFoundException(){
            NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
                when(bookRepository.findById(bookCorrect.getId())).thenReturn(Optional.empty());
                Integer quantityToBeAdd = 2;
                bookService.addCopiesOfBookToStock(quantityToBeAdd, bookCorrect.getId());
            });
            assertEquals(BOOK_NOT_FOUND_MESSAGE, thrown.getMessage());
        }

        @Test
        public void whenTryingToAddAnAmountOfCopiesToTheStockShouldAddCorrectly() throws BadRequestException, NotFoundException {
            when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(bookCorrectPersisted));
            Integer quantityToBeAdd = 2;
            bookService.addCopiesOfBookToStock(quantityToBeAdd, 1L);
            verify(bookRepository).saveAndFlush(bookCorrectPersisted);
        }
    }

    @Nested
    class DeleteCopiesOfBookToStockFunctionTests {
        @Test
        public void whenTryingToDeleteANegativeAmountOfCopiesInTheStockABadRequestExceptionShouldBeThrown(){
            BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
                when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(bookCorrectPersisted));
                bookService.deleteCopiesOfBookFromStock(-1, 1L);
            });
            assertEquals(BOOK_UNABLE_NEGATIVE_QUANTITY_TO_STOCK_MESSAGE, thrown.getMessage());
        }

        @Test
        public void whenTryingToDeleteAnAmountOfCopiesInTheStockOfABookThatDoesNotExistInTheDatabaseItShouldThrowAnNotFoundException(){
            NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
                when(bookRepository.findById(bookCorrect.getId())).thenReturn(Optional.empty());
                Integer quantityToBeDelete = 2;
                bookService.deleteCopiesOfBookFromStock(quantityToBeDelete, bookCorrect.getId());
            });
            assertEquals(BOOK_NOT_FOUND_MESSAGE, thrown.getMessage());
        }

        @Test
        public void whenTryingToDeleteAQuantityAboveOfTheAmountOfCopiesInTheStockABadRequestExceptionShouldBeThrown(){
            BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
                when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(bookCorrectPersisted));
                Integer quantityInStock = bookCorrectPersisted.getStock();
                assertEquals(10, quantityInStock);
                bookService.deleteCopiesOfBookFromStock(11, 1L);
            });
            assertEquals(BOOK_INSUFFICIENT_QUANTITY_IN_STOCK_MESSAGE, thrown.getMessage());
        }

        @Test
        public void whenTryingToDeleteAnAmountOfCopiesInTheStockShouldDeleteCorrectly() throws BadRequestException, NotFoundException {
            when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(bookCorrectPersisted));
            Integer quantityToBeDelete = 2;
            bookService.deleteCopiesOfBookFromStock(quantityToBeDelete, 1L);
            verify(bookRepository).saveAndFlush(bookCorrectPersisted);
        }
    }
}
