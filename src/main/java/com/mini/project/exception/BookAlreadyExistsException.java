package com.mini.project.exception;

public class BookAlreadyExistsException extends Exception{

    public BookAlreadyExistsException(String message){
        super(message);
    }
}
