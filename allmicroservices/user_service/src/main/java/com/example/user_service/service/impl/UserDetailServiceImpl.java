package com.example.user_service.service.impl;

import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.model.UserDetails;
import com.example.user_service.model.User;
import com.example.user_service.pojos.request.UserDetailsDTO;
import com.example.user_service.pojos.response.UserDetailResponse;
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
    public UserDetailResponse saveUserDetail(String id, UserDetailsDTO userDetailsDTO) throws ResourceNotFoundException {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Saving user details for user id : {}",id);
            Optional<User> user = Optional.ofNullable(userRepository.getUserById(id));
            if (user.isEmpty()) {
                logger.debug("User not found for id : {}",id);
                throw new ResourceNotFoundException(USER_NOT_FOUND);
            }
            UserDetails userDetails = user.get().getUserDetails();

            userDetails.setAge(userDetailsDTO.getAge());
            userDetails.setBloodGroup(userDetailsDTO.getBloodGroup());
            userDetails.setBio(userDetailsDTO.getBio());
            userDetails.setGender(userDetailsDTO.getGender());
            userDetails.setWeight(userDetailsDTO.getWeight());
            userDetails.setMaritalStatus(userDetailsDTO.getMaritalStatus());
            userDetails.setUserContact(userDetailsDTO.getUserContact());
            logger.info(RESPONSE_SAVED);
            userDetailsRepository.save(userDetails);
            logger.info(EXITING_METHOD_EXECUTION);
            return  new UserDetailResponse(SUCCESS,"Saved user details",userDetails);
        }

    }