package com.example.user_service.service;

import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.pojos.request.UserDetailsDTO;
import com.example.user_service.pojos.response.UserDetailResponse;

/**
 * This is an interface for user details service
 */
public interface UserDetailService {

    public UserDetailResponse saveUserDetail(String id, UserDetailsDTO userDetailsDTO) throws UserExceptionMessage, ResourceNotFoundException;
}