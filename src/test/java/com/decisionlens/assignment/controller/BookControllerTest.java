package com.decisionlens.assignment.controller;

import com.decisionlens.assignment.exception.BookAlreadyExistsException;
import com.decisionlens.assignment.exception.BookNotFoundException;
import com.decisionlens.assignment.exception.InvalidRequestException;
import com.decisionlens.assignment.model.Book;
import com.decisionlens.assignment.service.BookService;
import com.decisionlens.assignment.util.JsonUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = BookController.class)
public class BookControllerTest {
    @MockBean
    private BookService bookService;

    @Autowired
    private MockMvc mockMvc;

    private static final Long BOOK_ID = 1L;

    @Test
    @DisplayName("Retrieving book with valid book id")
    public void test_giveValidBookId_ReturnBook() throws Exception {

        Book returnBook = Book.builder().
                id(1L)
                .title("Rajesh assignment")
                .author("Rajesh")
                .numberOfPages(1)
                .build();
        Mockito.when(bookService.fetchBookById(BOOK_ID)).thenReturn(returnBook);

        mockMvc.perform(get("/api/v1/book/{id}",BOOK_ID).
                        contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(returnBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Rajesh assignment"))
                .andExpect(jsonPath("$.author").value("Rajesh"))
                .andExpect(jsonPath("$.numberOfPages").value(1)
                );
    }

    @Test
    @DisplayName("Retrieving book with invalid book id")
    public void test_giveInvalidBookId_ReturnBookNotFoundException() throws Exception {

        Book returnBook = Book.builder().
                id(10L)
                .title("Rajesh assignment")
                .author("Rajesh")
                .numberOfPages(1)
                .build();
        Mockito.when(bookService.fetchBookById(BOOK_ID)).thenReturn(returnBook);
        Mockito.doThrow(new BookNotFoundException("Book", "id", 10)).when(bookService).fetchBookById(BOOK_ID);

        MvcResult result= mockMvc.perform(get("/api/v1/book/{id}",BOOK_ID).
                        contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(returnBook)))
                .andExpect(status().isNotFound()
                ).andReturn();
        Assert.isTrue(result.getResponse().getContentAsString().contains("not found"), "Book not found with id : 10");
    }

    @Test
    @DisplayName("Retrieving all books")
    public void test_whenGetAllEndpointInvoke_ReturnBooks() throws Exception {
        List<Book> listOfBooks = new ArrayList<>();
        Book returnBook = Book.builder().
                id(1L)
                .title("Rajesh assignment")
                .author("Rajesh")
                .numberOfPages(1)
                .build();
        listOfBooks.add(returnBook);
        Mockito.when(bookService.getAllBooks()).thenReturn(listOfBooks);

        mockMvc.perform(get("/api/v1/book").
                        contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(listOfBooks)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].title").value("Rajesh assignment"))
                .andExpect(jsonPath("$.[0].author").value("Rajesh"))
                .andExpect(jsonPath("$.[0].numberOfPages").value(1)
                );
    }
    @Test
    @DisplayName("Adding book with valid input and book create successfully")
    public void test_giveValidBookDetails_AddToRepo() throws Exception {

        Book createBook = Book.builder().title("Rajesh assignment")
                .author("Rajesh")
                .numberOfPages(1)
                .build();
        Mockito.when(bookService.addBook(createBook)).thenReturn(createBook);

        mockMvc.perform(post("/api/v1/book").
                         contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(createBook)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.title").value("Rajesh assignment"))
                        .andExpect(jsonPath("$.author").value("Rajesh"))
                        .andExpect(jsonPath("$.numberOfPages").value(1)
                );
        Mockito.verify(bookService, times(1)).addBook(Mockito.any());
    }

    @Test
    @DisplayName("Adding book with same title again")
    public void test_giveValidBookDetails_WhenTitleAlreadyExists() throws Exception {

        Book createBook = Book.builder().title("Rajesh assignment")
                .author("Rajesh")
                .numberOfPages(1)
                .build();
        Mockito.doThrow(new BookAlreadyExistsException("Book with title " +createBook.getTitle()+ "already exists")).when(bookService).addBook(createBook);

        MvcResult result = mockMvc.perform(post("/api/v1/book").
                        contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(createBook)))
                .andExpect(status().isConflict()
                ).andReturn();
        Assert.isTrue(result.getResponse().getContentAsString().contains("already exists"),
                "Book with title " +createBook.getTitle()+ "already exists");
        Mockito.verify(bookService, times(1)).addBook(Mockito.any());
    }

    @Test
    @DisplayName("Adding book with empty title or author")
    public void test_giveInvalidBookDetails_ReturnsInvalidException() throws Exception {

        Book createBook = Book.builder().title("Rajesh assignment")
                .author("")
                .numberOfPages(1)
                .build();
        Mockito.doThrow(new InvalidRequestException("Adding Book input is not valid")).when(bookService).addBook(createBook);

        MvcResult result = mockMvc.perform(post("/api/v1/book").
                        contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(createBook)))
                .andExpect(status().isBadRequest()).andReturn();
        Assert.isTrue(result.getResponse().getContentAsString().contains("not valid"), "Adding Book input is not valid");
        Mockito.verify(bookService, times(1)).addBook(Mockito.any());
    }

    @Test
    @DisplayName("Updating book which is not existing")
    public void test_updateBook_WhenBookIdNotFound_CreateNewBook() throws Exception {

        Book createBook = Book.builder().title("Rajesh assignment")
                .author("Rajesh")
                .numberOfPages(1)
                .build();
        Mockito.when(bookService.fetchBookById(BOOK_ID)).thenReturn(new Book());
        Mockito.when(bookService.updateBook(BOOK_ID, createBook)).thenReturn(createBook);

        mockMvc.perform(put("/api/v1/book/{id}", BOOK_ID).
                        contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(createBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Rajesh assignment"))
                .andExpect(jsonPath("$.author").value("Rajesh"))
                .andExpect(jsonPath("$.numberOfPages").value(1)
                );
    }

    @Test
    @DisplayName("Updating book which is existing")
    public void test_updateBook_WhenBookIdFound() throws Exception {

        Book createBook = Book.builder().title("Rajesh assignment")
                .author("Rajesh")
                .numberOfPages(1)
                .build();

        Book updatedBook = Book.builder().title("Rajesh updated test")
                .author("Rajesh")
                .numberOfPages(10)
                .build();

        Mockito.when(bookService.fetchBookById(BOOK_ID)).thenReturn(createBook);
        Mockito.when(bookService.updateBook(BOOK_ID, createBook)).thenReturn(updatedBook);

        mockMvc.perform(put("/api/v1/book/{id}", BOOK_ID).
                        contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(createBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Rajesh updated test"))
                .andExpect(jsonPath("$.author").value("Rajesh"))
                .andExpect(jsonPath("$.numberOfPages").value(10)
                );
    }


    @Test
    @DisplayName("Delete book when ID is exists")
    public void test_deleteBook_whenBookIdFound() throws Exception {

        doNothing().when(bookService).removeBook(BOOK_ID);

        mockMvc.perform(delete("/api/v1/book/{id}", BOOK_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete book when ID is not exists")
    public void test_deleteBook_WhenBookIdNotFound_ReturnsBookNotFoundForDeleteException() throws Exception {

        Mockito.doThrow(new BookNotFoundException("Book id not found","for delete", 1)).when(bookService).removeBook(BOOK_ID);

        mockMvc.perform(delete("/api/v1/book/{id}", BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
