package com.decisionlens.assignment.Service;

import com.decisionlens.assignment.exception.BookAlreadyExistsException;
import com.decisionlens.assignment.exception.BookNotFoundException;
import com.decisionlens.assignment.exception.InvalidRequestException;
import com.decisionlens.assignment.model.Book;
import com.decisionlens.assignment.repo.BookRepository;
import com.decisionlens.assignment.service.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(BookServiceImpl.class)
public class BookServiceImplTest {

    private static final Long BOOK_ID = 1L;
    @MockBean
    BookRepository bookRepository;

    @Autowired
    BookServiceImpl sut;

    @Test
    @DisplayName("Testing get all service")
    public void test_getAllBooksFromRepo(){
        List<Book> listOfBooks = new ArrayList<>();
        Book returnBook = Book.builder().
                id(1L)
                .title("Rajesh assignment")
                .author("Rajesh")
                .numberOfPages(1)
                .build();
        listOfBooks.add(returnBook);
        when(bookRepository.findAll()).thenReturn(listOfBooks);

        List<Book> serviceCallBooks = sut.getAllBooks();
        assertTrue(serviceCallBooks.size()>0);
    }

    @Test
    @DisplayName("Testing fetch book by valid book id service")
    public void test_givenValidBookId_ReturnBook(){
        Book mockBook = Book.builder().
                id(1L)
                .title("Rajesh assignment")
                .author("Rajesh")
                .numberOfPages(1)
                .build();
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(mockBook));

        Book returnBook = sut.fetchBookById(BOOK_ID);
        assertEquals(mockBook.getTitle(),returnBook.getTitle());
    }

    @Test
    @DisplayName("Testing add book by in valid book id service")
    public void test_givenInValidBookId_ReturnBookNotFoundException(){
        Mockito.doThrow(new BookNotFoundException("Book", "id", 10)).when(bookRepository).findById(BOOK_ID);
        try{
            sut.fetchBookById(BOOK_ID);
        }catch (BookNotFoundException e) {
            assertEquals("Book not found id : 10", e.getMessage());
        }
    }

    @Test
    @DisplayName("Testing add book service by in valid details")
    public void test_givenInValidBookRequest_ReturnInvalidRequestException(){
        Book mockBook = Book.builder()
                .title("Rajesh assignment")
                .author("")
                .numberOfPages(1)
                .build();
        try{
            sut.addBook(mockBook);
        }catch (InvalidRequestException e) {
            assertEquals("Adding Book input is not valid", e.getMessage());
        }
    }

    @Test
    @DisplayName("Testing add book service with existing book details")
    public void test_givenExistingBookRequest_ReturnIBookAlreadyExistException(){
        Book mockBook = Book.builder()
                .id(1L)
                .title("Rajesh assignment")
                .author("Rajesh")
                .numberOfPages(1)
                .build();
        when(bookRepository.findByTitle(any())).thenReturn(Optional.of(mockBook));
        try{
            sut.addBook(mockBook);
        }catch (BookAlreadyExistsException e) {
            assertEquals("Book with title " +mockBook.getTitle()+ "already exists", e.getMessage());
        }
    }

    @Test
    @DisplayName("Testing update book service with existing book details")
    public void test_givenExistingBookIdWithValidDetails_returnUpdatedBook(){
        Book mockBook = Book.builder()
                .id(1L)
                .title("Rajesh assignment")
                .author("Rajesh")
                .numberOfPages(1)
                .build();
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(mockBook));
        when(bookRepository.save(mockBook)).thenReturn(mockBook);
        mockBook.setAuthor("Rajesh Reddy");
        Book updatedBook = sut.updateBook(BOOK_ID,mockBook);
        assertEquals(updatedBook.getAuthor(),"Rajesh Reddy");
    }

    @Test
    @DisplayName("Testing update book service without existing book details")
    public void test_givenNotExistingBookIdWithValidDetails_returnNewBook(){
        Book mockBook = Book.builder()
                .title("Rajesh assignment")
                .author("Rajesh")
                .numberOfPages(1)
                .build();
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.empty());
        when(bookRepository.save(mockBook)).thenReturn(mockBook);
        Book createdBook = sut.updateBook(BOOK_ID,mockBook);
        assertEquals(createdBook.getAuthor(),"Rajesh");
    }

    @Test
    @DisplayName("Testing remove book service with existing book id details")
    public void test_givenExistingBookId_deleteBook(){
        Book mockBook = Book.builder()
                .id(1L)
                .title("Rajesh assignment")
                .author("Rajesh")
                .numberOfPages(1)
                .build();
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(mockBook));
        doNothing().when(bookRepository).delete(mockBook);
        sut.removeBook(BOOK_ID);
       verify(bookRepository, times(1)).delete(mockBook);
    }

    @Test
    @DisplayName("Testing remove book service with not existing book id details")
    public void test_givenNotExistingBookId_returnBookNotFoundException(){
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.empty());
        try{
            sut.removeBook(BOOK_ID);
        }catch (BookNotFoundException e) {
            assertEquals("Book id not found for delete : 1", e.getMessage());
        }
    }
}
