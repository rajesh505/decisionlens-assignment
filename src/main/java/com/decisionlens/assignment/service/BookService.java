package com.decisionlens.assignment.service;

import com.decisionlens.assignment.model.Book;

import java.util.List;

public interface BookService {

    List<Book> getAllBooks();
    Book fetchBookById(Long bookId);
    Book addBook(Book book);
    Book updateBook(Long bookId, Book book);
    void removeBook(Long bookId);
}
