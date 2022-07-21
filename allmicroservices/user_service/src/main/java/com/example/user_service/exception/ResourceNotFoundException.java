package com.example.user_service.exception;

public class ResourceNotFoundException extends Exception{

    public ResourceNotFoundException(final String message){
        super(message);
    }
}
