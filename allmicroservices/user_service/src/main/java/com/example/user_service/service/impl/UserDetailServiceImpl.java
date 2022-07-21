package com.example.user_service.service.impl;

import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.UserDetails;
import com.example.user_service.model.User;
import com.example.user_service.pojos.request.UserDetailsDTO;
import com.example.user_service.repository.UserDetailsRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.user_service.util.Constants.*;
import static com.example.user_service.util.Constants.LoggerConstants.*;

/**
 * This class contains all the business logic for the user detail controller
 */
@Service
public class UserDetailServiceImpl implements UserDetailService {

    private final UserDetailsRepository userDetailsRepository;

    private final UserRepository userRepository;


    Logger logger = LoggerFactory.getLogger(UserDetailServiceImpl.class);
    public UserDetailServiceImpl(UserDetailsRepository userDetailsRepository, UserRepository userRepository){
        this.userDetailsRepository=userDetailsRepository;
        this.userRepository= userRepository;
    }
    @Override
    public UserDetails saveUserDetail(String id, UserDetailsDTO userDetailsDTO) throws UserExceptionMessage, ResourceNotFoundException {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Saving user details for user id : {}",id);
            Optional<User> user = Optional.ofNullable(userRepository.getUserById(id));
            if (user.isEmpty()) {
                logger.debug("User not found for id : {}",id);
                throw new ResourceNotFoundException(USER_NOT_FOUND);
            }
            UserDetails userDetails1 = user.get().getUserDetails();

            userDetails1.setAge(userDetailsDTO.getAge());
            userDetails1.setBloodGroup(userDetailsDTO.getBloodGroup());
            userDetails1.setBio(userDetailsDTO.getBio());
            userDetails1.setGender(userDetailsDTO.getGender());
            userDetails1.setWeight(userDetailsDTO.getWeight());
            userDetails1.setMaritalStatus(userDetailsDTO.getMaritalStatus());
            userDetails1.setUserContact(userDetailsDTO.getUserContact());
            logger.info(RESPONSE_SAVED);
            userDetailsRepository.save(userDetails1);
            logger.info(EXITING_METHOD_EXECUTION);
            return userDetails1;
        }

    }