package com.mnader.library_challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnader.library_challenge.controller.exceptions.BadRequestException;
import com.mnader.library_challenge.controller.exceptions.NotFoundException;
import com.mnader.library_challenge.model.Book;
import com.mnader.library_challenge.model.DTO.BookRequestDTO;
import com.mnader.library_challenge.model.DTO.BookResponseDTO;
import com.mnader.library_challenge.repository.BookRepository;
import com.mnader.library_challenge.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LibraryController.class)
public class LibraryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;

    private BookRequestDTO bookCorrectRequestDTO;
    private BookRequestDTO bookWithoutTitleRequestDTO;
    private BookRequestDTO bookWithTitleWith2charactersRequestDTO;
    private BookRequestDTO bookWithTitleWith256charactersRequestDTO;
    private BookRequestDTO bookWithNegativePriceRequestDTO;
    private BookRequestDTO bookWithNegativeStockRequestDTO;
    private BookResponseDTO bookCorrectResponseDTO;
    private Book bookCorrectPersisted;

    public LibraryControllerTest() {
    }

    @BeforeEach
    public void setupBefore() {
        MockitoAnnotations.openMocks(this);

        this.bookCorrectRequestDTO = BookRequestDTO.builder()
            .title("title")
            .author("author")
            .isbn("isbn")
            .price(99.90)
            .stock(10)
            .build();

        this.bookWithoutTitleRequestDTO = BookRequestDTO.builder()
            .title("")
            .author("author")
            .isbn("isbn")
            .price(99.90)
            .stock(10)
            .build();

        this.bookWithTitleWith2charactersRequestDTO = BookRequestDTO.builder()
            .title("be")
            .author("author")
            .isbn("isbn")
            .price(99.90)
            .stock(10)
            .build();

        this.bookWithTitleWith256charactersRequestDTO = BookRequestDTO.builder()
            .title(
                "Lorem Nam quis. Integer malesuada. " +
                "In in enim a arcu imperdiet malesuada. " +
                "Sed vel lectus. Donec odio urna, tempus molestie, porttitor ut, iaculis quis, sem. " +
                "Phasellus rhoncus. Aenean id metus id velit ullamcorper pulvinar. Vestibulum fermentum tortor id mi"
            )
            .author("author")
            .isbn("isbn")
            .price(99.90)
            .stock(10)
            .build();

        this.bookWithNegativePriceRequestDTO = BookRequestDTO.builder()
            .title("title")
            .author("author")
            .isbn("isbn")
            .price(-99.90)
            .stock(10)
            .build();

        this.bookWithNegativeStockRequestDTO = BookRequestDTO.builder()
            .title("title")
            .author("author")
            .isbn("isbn")
            .price(99.90)
            .stock(-10)
            .build();
        this.bookCorrectResponseDTO = BookResponseDTO.builder()
            .id(1L)
            .title("title")
            .author("author")
            .isbn("isbn")
            .price(99.90)
            .averageRating(0.0)
            .numberOfRatings(0)
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
    }

    @Nested
    class SaveBookFunctionTests {
        @Test
        public void saveBookWithSuccess() throws Exception
        {
            when(bookService.saveBook(bookCorrectRequestDTO.convertToEntity())).thenReturn(bookCorrectPersisted);

            mockMvc.perform(MockMvcRequestBuilders
                    .post("/library/book")
                    .content(convertObjectToJsonFormat(bookCorrectRequestDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string(convertObjectToJsonFormat(bookCorrectResponseDTO)));
        }

        @Test
        public  void saveBookWithoutBookShouldThrowMethodArgumentNotValidException() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/library/book")
                    .content(convertObjectToJsonFormat(bookWithoutTitleRequestDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
                    assertTrue(result.getResponse().getContentAsString().contains("The title must be between 3 and 255 characters"));
                    assertTrue(result.getResponse().getContentAsString().contains("Title is mandatory"));
                }).andReturn();
        }

        @Test
        public  void saveBookWithATitleWith2CharactersShouldThrowMethodArgumentNotValidException() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/library/book")
                    .content(convertObjectToJsonFormat(bookWithTitleWith2charactersRequestDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
                    assertTrue(result.getResponse().getContentAsString().contains("The title must be between 3 and 255 characters"));
                }).andReturn();
        }

        @Test
        public  void saveBookWithATitleWith256CharactersShouldThrowMethodArgumentNotValidException() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/library/book")
                    .content(convertObjectToJsonFormat(bookWithTitleWith256charactersRequestDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
                    assertTrue(result.getResponse().getContentAsString().contains("The title must be between 3 and 255 characters"));
                }).andReturn();
        }

        @Test
        public  void saveBookWithWithNegativePriceShouldThrowMethodArgumentNotValidException() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/library/book")
                    .content(convertObjectToJsonFormat(bookWithNegativePriceRequestDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
                    assertTrue(result.getResponse().getContentAsString().contains("must be greater than or equal to 0"));
                }).andReturn();
        }

        @Test
        public  void saveBookWithWithNegativeStockShouldThrowMethodArgumentNotValidException() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/library/book")
                    .content(convertObjectToJsonFormat(bookWithNegativeStockRequestDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
                    assertTrue(result.getResponse().getContentAsString().contains("must be greater than or equal to 0"));
                }).andReturn();
        }
    }

    @Nested
    class FindBooksByTitleFunctionTests {
        @Test
        public void findBooksByTitleWithSuccess() throws Exception
        {
            List<Book> expectedBookList = List.of(bookCorrectPersisted, bookCorrectPersisted);
            List<BookResponseDTO> bookResponseDTOList = expectedBookList.stream().map(Book::convertToResponseDTO).toList();
            when(bookService.findBooksByTitle("title")).thenReturn(expectedBookList);

            mockMvc.perform(MockMvcRequestBuilders
                    .get("/library/book/title"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(convertObjectToJsonFormat(bookResponseDTOList)));
        }
    }

    @Nested
    class DeleteBookByIDFunctionTests {
        @Test
        public void deleteBookByIDWithSuccess() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                    .delete("/library/book/1"))
                .andExpect(status().isNoContent());
            verify(bookService).deleteBookByID(1L);
        }
    }

    @Nested
    class UpdateBookByIDFunctionTests {
        @Test
        public void updateBookByIDWithSuccess() throws Exception {
            when(bookService.updateBookByID(1L, bookCorrectRequestDTO.convertToEntity())).thenReturn(bookCorrectPersisted);
            mockMvc.perform(MockMvcRequestBuilders
                    .put("/library/book/1")
                    .content(convertObjectToJsonFormat(bookCorrectRequestDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(convertObjectToJsonFormat(bookCorrectResponseDTO)));
        }
    }

    @Nested
    class RateBookByIDFunctionTests {
        @Test
        public void rateBookByIDWithSuccess() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                    .put("/library/book/1/rate_book?rating=3")
                )
                .andExpect(status().isOk());
            verify(bookService).rateBook(3.0, 1L);
        }
    }

    @Nested
    class AddCopiesOfBookToStockFunctionTests {
        @Test
        public void addCopiesOfBookToStockWithoutSpecificQuantityInQueryStringWithSuccess() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                    .put("/library/book/1/add_copies")
                )
                .andExpect(status().isOk());
            verify(bookService).addCopiesOfBookToStock(1, 1L);
        }

        @Test
        public void addCopiesOfBookToStockSpecifyingQuantityInQueryStringWithSuccess() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                    .put("/library/book/1/add_copies?quantity=10")
                )
                .andExpect(status().isOk());
            verify(bookService).addCopiesOfBookToStock(10, 1L);
        }
    }

    @Nested
    class DeleteCopiesOfBookToStockFunctionTests {
        @Test
        public void deleteCopiesOfBookToStockWithoutSpecificQuantityInQueryStringWithSuccess() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                    .put("/library/book/1/delete_copies")
                )
                .andExpect(status().isOk());
            verify(bookService).deleteCopiesOfBookFromStock(1, 1L);
        }

        @Test
        public void deleteCopiesOfBookToStockSpecifyingQuantityInQueryStringWithSuccess() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                    .put("/library/book/1/delete_copies?quantity=10")
                )
                .andExpect(status().isOk());
            verify(bookService).deleteCopiesOfBookFromStock(10, 1L);
        }
    }

    public static String convertObjectToJsonFormat(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

