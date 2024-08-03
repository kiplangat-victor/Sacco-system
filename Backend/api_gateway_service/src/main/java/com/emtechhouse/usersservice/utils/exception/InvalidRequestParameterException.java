package com.emtechhouse.usersservice.utils.exception;

public class InvalidRequestParameterException extends RuntimeException{

    public InvalidRequestParameterException(String message) {
        super(message);
    }
}
