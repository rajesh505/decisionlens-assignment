package com.decisionlens.assignment.service;

import com.decisionlens.assignment.exception.BookAlreadyExistsException;
import com.decisionlens.assignment.exception.BookNotFoundException;
import com.decisionlens.assignment.exception.InvalidRequestException;
import com.decisionlens.assignment.model.Book;
import com.decisionlens.assignment.repo.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Service
public class BookServiceImpl implements BookService{

    private final BookRepository bookRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book fetchBookById(Long bookId) {
        return bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book", "id", bookId));
    }

    @Override
    public Book addBook(Book book) {
        boolean validRequest = validateAddingBook(book);
        if(!validRequest){
            throw new InvalidRequestException("Adding Book input is not valid");
        }
        if(bookRepository.findByTitle(book.getTitle()).isPresent()){
            throw new BookAlreadyExistsException("Book with title " +book.getTitle()+ "already exists");
        }
        return bookRepository.save(book);
    }

    private boolean validateAddingBook(Book book) {
        if(book.getTitle() == null || book.getAuthor() == null){
            return false;
        }
        return true;
    }

    @Override
    public Book updateBook(Long bookId, Book bookDetails) {
       return bookRepository.findById(bookId)
                .map(existingBook -> {
                    existingBook.setTitle(bookDetails.getTitle());
                    existingBook.setAuthor(bookDetails.getAuthor());
                    existingBook.setNumberOfPages(bookDetails.getNumberOfPages());
                    existingBook.setPublishedDate(new Date());
                    return bookRepository.save(existingBook);
                })
                .orElseGet(() ->
                         bookRepository.save(bookDetails));
    }
    @Override
    public void removeBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book id", "for delete", bookId));
        bookRepository.delete(book);
    }
}
