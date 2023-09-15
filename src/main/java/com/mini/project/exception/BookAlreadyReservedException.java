package com.mini.project.exception;

public class BookAlreadyReservedException extends Exception{

    public BookAlreadyReservedException(String message){
        super(message);
    }
}
