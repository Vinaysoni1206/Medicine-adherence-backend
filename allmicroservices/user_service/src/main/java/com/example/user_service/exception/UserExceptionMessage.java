package com.example.user_service.exception;

/**
 *  Sends exception message for User
 */
public class UserExceptionMessage extends Exception{

     public UserExceptionMessage(final String errorMessage){
         super(errorMessage);
     }

}
