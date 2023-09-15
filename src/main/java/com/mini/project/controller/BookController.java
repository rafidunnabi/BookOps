package com.mini.project.controller;

import com.mini.project.exception.*;
import com.mini.project.model.BookDto;
import com.mini.project.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("/all")
    public ResponseEntity<List<BookDto>> getAllBooks() throws NoBookFoundException {
        List<BookDto> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @PostMapping("/create")
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto book) throws BookAlreadyExistsException {
        BookDto createdBook = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @RequestBody BookDto updatedBook) throws BookNotFoundException{
        BookDto updated = bookService.updateBook(id, updatedBook);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) throws BookNotFoundException{
        bookService.deleteBook(id);
        String message = "Book with id " + id + " has been deleted.";
        return ResponseEntity.ok(message);
    }

    @PostMapping("/{bookId}/borrow")
    public ResponseEntity<String> borrowBook(
            @PathVariable Long bookId) throws BookNotFoundException, UserNotFoundException, BookNotAvailableException{

        bookService.borrowBook(bookId);
        return ResponseEntity.ok("Book with ID " + bookId + " has been borrowed.");
    }

    @PostMapping("/{bookId}/return")
    public ResponseEntity<String> returnBook(
            @PathVariable Long bookId) throws BookNotFoundException, BookNotBorrowedException, BorrowBookNotFoundException {
        bookService.returnBook(bookId);
        return ResponseEntity.ok("Book with ID " + bookId + " has been returned.");
    }

}
