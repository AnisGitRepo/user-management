package com.management.exception;


public class UnauthorizedOperationException extends RuntimeException{
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
