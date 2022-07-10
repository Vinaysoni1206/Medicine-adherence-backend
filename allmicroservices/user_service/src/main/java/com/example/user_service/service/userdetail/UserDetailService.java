package com.example.user_service.service.userdetail;

import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.user.UserDetails;
import com.example.user_service.pojos.dto.user.UserDetailsDTO;


public interface UserDetailService {

    public UserDetails saveUserDetail(String id, UserDetailsDTO userDetailsDTO) throws  UserExceptionMessage;
}