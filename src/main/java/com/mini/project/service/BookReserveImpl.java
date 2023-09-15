package com.mini.project.service;

import com.mini.project.entity.BookEntity;
import com.mini.project.entity.ReservationEntity;
import com.mini.project.entity.UserEntity;
import com.mini.project.exception.*;
import com.mini.project.repository.BookRepository;
import com.mini.project.repository.BorrowBookRepository;
import com.mini.project.repository.ReservationRepository;
import com.mini.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class BookReserveImpl implements BookReserve{

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BorrowBookRepository borrowBookRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    public void reserveBook(Long bookId) throws BookNotFoundException, BookAlreadyAvailableException, UserNotFoundException, ReservationAlreadyExistsException, CannotReserveBorrowedBookException{

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> user = userRepository.findByEmail(authentication.getName());

        BookEntity book = bookRepository.findByIdAndIsDeletedFalse(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));

        boolean isBookAvailable = borrowBookRepository.existsByBookEntityAndIsAvailable(book, true);

        ReservationEntity reservation = ReservationEntity.builder()
                .book(book)
                .user(user.get())
                .reservationTime(LocalDateTime.now())
                .isReserved(isBookAvailable) // Set isReserved based on book availability
                .isCancelled(false)
                .notify(!isBookAvailable ? "Notified" : "Pending") // Set notify status
                .build();
        reservationRepository.save(reservation);
    }


    public void cancelReservation(Long bookId) throws BookNotFoundException, UserNotFoundException, ReservationNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> user = userRepository.findByEmail(authentication.getName());
        Long userId = user.get().getId();

        BookEntity book = bookRepository.findByIdAndIsDeletedFalse(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        ReservationEntity reservation = reservationRepository.findByUserAndBookAndIsCancelledFalse(userEntity, book)
                .orElseThrow(() -> new ReservationNotFoundException("No active reservation found for this book."));

        reservation.setCancelled(true);
        reservationRepository.save(reservation);
    }
}
