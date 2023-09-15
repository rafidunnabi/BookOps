package com.mini.project.service;

import com.mini.project.entity.BookEntity;
import com.mini.project.exception.*;
import com.mini.project.model.BookDto;

import java.util.List;

public interface BookService {
    public List<BookDto> getAllBooks() throws NoBookFoundException;

    public BookDto createBook(BookDto book) throws BookAlreadyExistsException;

    public BookDto updateBook(Long id, BookDto updatedBook) throws BookNotFoundException;

    public void deleteBook(Long id) throws BookNotFoundException;
    public void borrowBook(Long bookId) throws BookNotFoundException, BookNotAvailableException, UserNotFoundException;

    public void returnBook(Long bookId) throws BookNotBorrowedException, BookNotFoundException, BorrowBookNotFoundException;

}
