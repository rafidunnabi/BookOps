package com.mini.project.service;


import com.mini.project.exception.NoPermissionException;
import com.mini.project.exception.UserNotFoundException;
import com.mini.project.model.BookDto;
import com.mini.project.model.BorrowDto;
import com.mini.project.model.UserDto;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto createUser(UserDto user) throws Exception;
    UserDto getUser(String email);
    UserDto getUserById(Long id) throws UserNotFoundException;

    public List<BookDto> getUserBorrowedBooksHistory(Long userId) throws UserNotFoundException;

    public Set<BookDto> getCurrentlyBorrowedBooks(Long userId);

    public List<BorrowDto> getUserBorrowingHistory(Long userId) throws NoPermissionException;

}
