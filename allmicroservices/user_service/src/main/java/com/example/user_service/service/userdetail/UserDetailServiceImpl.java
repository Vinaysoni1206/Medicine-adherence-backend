package com.example.user_service.service.userdetail;

import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.user.UserDetails;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.pojos.dto.user.UserDetailsDTO;
import com.example.user_service.repository.UserDetailsRepository;
import com.example.user_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.user_service.util.Messages.*;
import static com.example.user_service.util.Messages.LoggerConstants.*;

@Service
public class UserDetailServiceImpl implements UserDetailService {

    private final UserDetailsRepository userDetailsRepository;

    private final UserRepository userRepository;


    Logger logger = LoggerFactory.getLogger(UserDetailServiceImpl.class);
    UserDetailServiceImpl(UserDetailsRepository userDetailsRepository, UserRepository userRepository){
        this.userDetailsRepository=userDetailsRepository;
        this.userRepository= userRepository;
    }
    @Override
    public UserDetails saveUserDetail(String id, UserDetailsDTO userDetailsDTO) throws UserExceptionMessage {
        logger.info(STARTING_METHOD_EXECUTION);
            Optional<UserEntity> user = Optional.ofNullable(userRepository.getUserById(id));
            if (user.isEmpty()) {
                throw new UserExceptionMessage(DATA_NOT_FOUND);
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