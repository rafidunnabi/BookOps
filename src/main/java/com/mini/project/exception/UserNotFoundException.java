package com.mini.project.exception;

import org.apache.catalina.User;

public class UserNotFoundException extends Exception{

    public UserNotFoundException(String message){
        super(message);
    }
}
