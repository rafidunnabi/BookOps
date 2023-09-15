package com.mini.project.exception;

public class BookNotBorrowedException extends Exception{
    public BookNotBorrowedException(String message) {
        super(message);
    }
}
