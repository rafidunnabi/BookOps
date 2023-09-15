package com.mini.project.service;


import com.mini.project.entity.BookEntity;
import com.mini.project.entity.BorrowBookEntity;
import com.mini.project.entity.UserEntity;
import com.mini.project.exception.NoPermissionException;
import com.mini.project.exception.UserNotFoundException;
import com.mini.project.model.BookDto;
import com.mini.project.model.BorrowDto;
import com.mini.project.model.UserDto;
import com.mini.project.repository.BorrowBookRepository;
import com.mini.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private BorrowBookRepository borrowBookRepository;

    @Override
    public UserDto createUser(UserDto user) throws Exception {
        ModelMapper modelMapper = new ModelMapper();
        if(userRepository.findByEmail(user.getEmail()).isPresent())
            throw new Exception("Record already exists");
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setAddress(user.getAddress());
        userEntity.setRole(user.getRole());
        UserEntity storedUserDetails =userRepository.save(userEntity);
        UserDto returnedValue = modelMapper.map(storedUserDetails,UserDto.class);
        return returnedValue;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email).get();
        if(userEntity == null) throw new UsernameNotFoundException("No record found");
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity,returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUserById(Long id) throws UserNotFoundException {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email).get();
        if(userEntity==null) throw new UsernameNotFoundException(email);
        return new User(userEntity.getEmail(),userEntity.getPassword(),
                true,true,true,true,new ArrayList<>());
    }

public List<BookDto> getUserBorrowedBooksHistory(Long userId) throws UserNotFoundException{

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Optional<UserEntity> user = userRepository.findByEmail(authentication.getName());
    String userRole = user.get().getRole();

    boolean isAdmin = "ADMIN".equals(userRole);
    boolean isCustomer = "CUSTOMER".equals(userRole) && userId.equals(user.get().getId());

    if (!isAdmin && !isCustomer) {
        throw new AccessDeniedException("You do not have permission to access this user's books.");
    }
    List<BorrowBookEntity> borrowRecords = borrowBookRepository.findByUserEntity_Id(userId);

    List<BorrowBookEntity> borrowedBooks = borrowRecords.stream()
            .filter(borrow -> !borrow.isAvailable()) // Check isAvailable and returnDate
            .collect(Collectors.toList());

    List<BookDto> currentlyBorrowedBooks = new ArrayList<>();
    for (BorrowBookEntity borrowBookEntity : borrowedBooks) {
        BookEntity book = borrowBookEntity.getBookEntity();
        currentlyBorrowedBooks.add(BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .build());
    }
    return currentlyBorrowedBooks;
}
    public Set<BookDto> getCurrentlyBorrowedBooks(Long userId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> user = userRepository.findByEmail(authentication.getName());
        String userRole = user.get().getRole();

        boolean isAdmin = "ADMIN".equals(userRole);
        boolean isCustomer = "CUSTOMER".equals(userRole) && userId.equals(user.get().getId());

        if (!isAdmin && !isCustomer) {
            throw new AccessDeniedException("You do not have permission to access this user's books.");
        }
        List<BorrowBookEntity> borrowRecords = borrowBookRepository.findByUserEntity_Id(userId);
        List<BorrowBookEntity> borrowedBooks = borrowRecords.stream()
                .filter(borrow -> !borrow.isAvailable())
                .collect(Collectors.toList());

        Set<BookDto> currentlyBorrowedBooks = new HashSet<>();
        for (BorrowBookEntity borrowBookEntity : borrowedBooks) {
            BookEntity book = borrowBookEntity.getBookEntity();
            currentlyBorrowedBooks.add(BookDto.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .build());
        }
        return currentlyBorrowedBooks;
    }

    public List<BorrowDto> getUserBorrowingHistory(Long givenUserId) throws com.mini.project.exception.NoPermissionException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> user = userRepository.findByEmail(authentication.getName());
        Long userId = user.get().getId();
        String userRole = user.get().getRole();

        boolean isCustomer = "CUSTOMER".equals(userRole) && givenUserId.equals(user.get().getId());
        List<BorrowBookEntity> borrowingRecords = borrowBookRepository.findByUserEntity_Id(userId);
        if (!isCustomer) {
            throw new NoPermissionException("You do not have permission to access this user's books.");
        }

        List<BorrowDto> borrowingHistory = borrowingRecords.stream()
                .map(borrowingRecord -> {
                    BookEntity book = borrowingRecord.getBookEntity();
                    return BorrowDto.builder()
                            .book_id(book.getId())
                            .title(book.getTitle())
                            .author(book.getAuthor())
                            .borrowDate(borrowingRecord.getBorrowDate())
                            .dueDate(borrowingRecord.getDueDate())
                            .returnDate(borrowingRecord.getReturnDate())
                            .build();
                })
                .collect(Collectors.toList());
        return borrowingHistory;
    }

    public BookDto convertToDto(BookEntity bookEntity) {
        BookDto bookDto = new BookDto();
        bookDto.setId(bookEntity.getId());
        bookDto.setTitle(bookEntity.getTitle());
        bookDto.setAuthor(bookEntity.getAuthor());
        return bookDto;
    }
}
