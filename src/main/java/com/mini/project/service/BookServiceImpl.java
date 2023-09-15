package com.mini.project.service;

import com.mini.project.entity.BookEntity;
import com.mini.project.entity.BorrowBookEntity;
import com.mini.project.entity.ReservationEntity;
import com.mini.project.entity.UserEntity;
import com.mini.project.exception.*;
import com.mini.project.model.BookDto;
import com.mini.project.model.UserDto;
import com.mini.project.repository.BookRepository;
import com.mini.project.repository.BorrowBookRepository;
import com.mini.project.repository.ReservationRepository;
import com.mini.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BorrowBookRepository borrowBookRepository;

    public List<BookDto> getAllBooks() throws NoBookFoundException{

        try {
            return bookRepository.findAll().stream()
                    .filter(bookEntity -> !bookEntity.isDeleted()) // Filter out soft-deleted books
                    .map(bookEntity -> {
                        BookDto bookDto = new BookDto();
                        bookDto.setId(bookEntity.getId());
                        bookDto.setTitle(bookEntity.getTitle());
                        bookDto.setAuthor(bookEntity.getAuthor());
                        return bookDto;
                    })
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new NoBookFoundException("No Book Found.");
        }

    }

    public BookDto createBook(BookDto book) throws BookAlreadyExistsException {
        ModelMapper modelMapper = new ModelMapper();
        BookEntity bookEntity = new BookEntity();
        bookEntity.setTitle(book.getTitle());
        bookEntity.setAuthor(book.getAuthor());
        BookEntity storedBookDetails =bookRepository.save(bookEntity);
        BookDto returnedValue = modelMapper.map(storedBookDetails,BookDto.class);
        return returnedValue;
    }

    public BookDto updateBook(Long id, BookDto updatedBook) throws BookNotFoundException{
        BookEntity existingBook = bookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());

        BookEntity updatedEntity = bookRepository.save(existingBook);
        BookDto updatedDto = new BookDto();
        BeanUtils.copyProperties(updatedEntity, updatedDto);
        return updatedDto;
    }

    public void deleteBook(Long id) throws BookNotFoundException{

        Optional<BookEntity> optionalBook = bookRepository.findByIdAndIsDeletedFalse(id);
        BookEntity book = optionalBook.orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));

        book.setDeleted(true);
        bookRepository.save(book);

    }

    public void borrowBook(Long bookId) throws BookNotFoundException, BookNotAvailableException, UserNotFoundException{

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> user = userRepository.findByEmail(authentication.getName());
        Long userId = user.get().getId();
        // Find the book by its ID
        BookEntity book = bookRepository.findByIdAndIsDeletedFalse(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));

        // Find the user by their ID
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        boolean alreadyBorrowed = borrowBookRepository.existsByBookEntityAndReturnDateIsNull(book);
        if (alreadyBorrowed) {
            throw new BookNotAvailableException("User already borrowed the book with ID " + bookId);
        }
        // Create a BorrowBookEntity to track the borrowing information
        BorrowBookEntity borrowBook = new BorrowBookEntity();
        borrowBook.setBookEntity(book);
        borrowBook.setUserEntity(userEntity);
        borrowBook.setBorrowDate(LocalDate.now());
        borrowBook.setDueDate(LocalDate.now().plusDays(14)); // Due date from the book
        borrowBook.setAvailable(false);
        borrowBookRepository.save(borrowBook);
    }

    public void returnBook(Long bookId) throws BookNotBorrowedException, BookNotFoundException, BorrowBookNotFoundException{

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> user = userRepository.findByEmail(authentication.getName());
        Long userId = user.get().getId();

        BookEntity book = bookRepository.findByIdAndIsDeletedFalse(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));

        BorrowBookEntity borrowBook = borrowBookRepository.findByBookEntityAndReturnDateIsNull(book)
                .orElseThrow(() -> new BorrowBookNotFoundException("No active borrowing record found for book with ID: " + bookId));

        if (!borrowBook.getUserEntity().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to return this book.");
        }

        borrowBook.setReturnDate(LocalDate.now());
        borrowBook.setAvailable(true);
        borrowBookRepository.save(borrowBook);

        reservationRepository.findAllByBookAndNotifyAndIsCancelledFalse(book, "Pending")
                .forEach(r -> r.setNotify("Notified"));

    }
    public BookDto convertToDto(BookEntity bookEntity) {
        BookDto bookDto = new BookDto();
        bookDto.setId(bookEntity.getId());
        bookDto.setTitle(bookEntity.getTitle());
        bookDto.setAuthor(bookEntity.getAuthor());
        return bookDto;
    }
}
