package com.mini.project.service;

import com.mini.project.exception.*;

public interface BookReserve {
    public void reserveBook(Long bookId) throws BookNotFoundException, BookAlreadyAvailableException, UserNotFoundException, ReservationAlreadyExistsException, CannotReserveBorrowedBookException;
    public void cancelReservation(Long bookId) throws BookNotFoundException, UserNotFoundException, ReservationNotFoundException;

}
