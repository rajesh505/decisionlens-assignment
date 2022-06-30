package com.decisionlens.assignment.controller;

import com.decisionlens.assignment.errorhandling.ErrorResponse;
import com.decisionlens.assignment.model.Book;
import com.decisionlens.assignment.service.BookService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/book")
@AllArgsConstructor
public class BookController {
    private final BookService bookService;

    /**
     * Retrieve all books from database
     * @return
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Retrieves books")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Books found", response = Book.class),
            @ApiResponse(code = 404, message = "Books not found", response = Book.class)
    })
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /**
     * Retrieve book based on book id
     * @param bookId
     * @return
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Retrieves books")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Books found", response = Book.class),
            @ApiResponse(code = 404, message = "Books not found", response = Book.class)
    })
    public ResponseEntity<Book> fetchBookById(@PathVariable("id") Long bookId) {
        return ResponseEntity.ok(bookService.fetchBookById(bookId));
    }

    /**
     * Add book details to database
     * @param book
     * @return Book
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creates new records of Book and only returns the ones saved correctly")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Book created", response = Book.class),
            @ApiResponse(code = 409, message = "Book not created", response = Book.class)
    })
    public Book createBook( @RequestBody Book book) {
            return bookService.addBook(book);
    }
    /**
     * Updates Book details .
     * - @returns updated book details.
     */
    @PutMapping(value ="/{id}", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Update a book based on it's ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Book updated correctly", response = Book.class),
            @ApiResponse(code = 404, message = "Book not found", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Bad request", response = ErrorResponse.class)
    })
    public Book updateBook(@PathVariable(value = "id") Long bookId,
                            @RequestBody Book bookDetails) {
        return bookService.updateBook(bookId, bookDetails);
    }

    /**
     * Delete book with book ID as input
     * @param bookId
     * @return
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Delete a book based on it's ID")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Book deleted correctly"),
            @ApiResponse(code = 404, message = "Book not found", response = ErrorResponse.class)
    })
    public ResponseEntity<?> deleteBook(@PathVariable("id") Long bookId) {
        bookService.removeBook(bookId);
        return ResponseEntity.noContent().build();
    }
}
