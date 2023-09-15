package com.mini.project.controller;

import com.mini.project.exception.*;
import com.mini.project.model.BookDto;
import com.mini.project.service.BookReserve;
import com.mini.project.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookReservationController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookReserve bookReserve;

    @PostMapping("/{bookId}/reserve")
    public ResponseEntity<String> reserveBook(
            @PathVariable Long bookId) throws BookNotFoundException, BookAlreadyAvailableException, UserNotFoundException, ReservationAlreadyExistsException, CannotReserveBorrowedBookException{
        bookReserve.reserveBook(bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Book reserved successfully.");
    }

    @PostMapping("/{bookId}/cancel-reservation")
    public ResponseEntity<String> cancelReservation(
            @PathVariable Long bookId) throws BookNotFoundException, UserNotFoundException, ReservationNotFoundException{
        bookReserve.cancelReservation(bookId);
        return ResponseEntity.status(HttpStatus.OK).body("Reservation canceled successfully.");
    }
}
