package com.mini.project.controller;


import com.mini.project.exception.NoPermissionException;
import com.mini.project.exception.UserNotFoundException;
import com.mini.project.model.BookDto;
import com.mini.project.model.BorrowDto;
import com.mini.project.model.UserDto;
import com.mini.project.model.UserRegistrationResponse;
import com.mini.project.service.UserService;
import com.mini.project.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) throws Exception {
        try {
            UserDto createdUser = userService.createUser(userDto);
            String accessToken = JWTUtils.generateToken(createdUser.getEmail());
            UserRegistrationResponse response = new UserRegistrationResponse(createdUser, accessToken);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDto> getUserDetails(@PathVariable Long userId) throws UserNotFoundException {
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/users/{userId}/books")
    public ResponseEntity<List<BookDto>> getUserBorrowedBooksHistory(@PathVariable Long userId) throws UserNotFoundException{
        List<BookDto> borrowedBooksHistory = userService.getUserBorrowedBooksHistory(userId);
        return ResponseEntity.ok(borrowedBooksHistory);
    }
    @GetMapping("/users/{userId}/borrowed-books")
    public ResponseEntity<Set<BookDto>> getCurrentlyBorrowedBooks(@PathVariable Long userId) {
        Set<BookDto> currentlyBorrowedBooks = userService.getCurrentlyBorrowedBooks(userId);
        return new ResponseEntity<>(currentlyBorrowedBooks, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/history")
    public ResponseEntity<List<BorrowDto>> getUserBorrowingHistory(@PathVariable Long userId) throws NoPermissionException {
        List<BorrowDto> borrowingHistory = userService.getUserBorrowingHistory(userId);
        return ResponseEntity.ok(borrowingHistory);
    }

}
