package com.example.user_service.service;

import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.UserDetails;
import com.example.user_service.pojos.request.UserDetailsDTO;

/**
 * This is an interface for user details service
 */
public interface UserDetailService {

    public UserDetails saveUserDetail(String id, UserDetailsDTO userDetailsDTO) throws UserExceptionMessage, ResourceNotFoundException;
}