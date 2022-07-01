package com.example.userService;

import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.pojos.response.user.UserDetailResponsePage;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    UserServiceImpl userServiceImpl;
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMedicineRepository userMedicineRepository;

   @BeforeEach
   public void initcase() {
       userServiceImpl= new UserServiceImpl(userRepository, userMedicineRepository);
   }

   @Test
   void getUsersTest() throws Exception{
       UserEntity userEntity = new UserEntity("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
       List<UserEntity> users = new ArrayList<>();
       users.add(userEntity);
       int pageSize = 1;
       int pageNo = 1;
       Pageable paging = PageRequest.of(pageNo,pageSize);
       Page<UserEntity> userEntityPage= new PageImpl<>(users);
       when(userRepository.findAll(paging)).thenReturn(userEntityPage);
       UserDetailResponsePage userResponsePage = new UserDetailResponsePage("Success","Data found",5L,2,1,users);
       CompletableFuture<UserDetailResponsePage> userResponsePage1=userServiceImpl.getUsers(1,1);
       Assertions.assertEquals(userResponsePage.getUserEntity(),userResponsePage1.get().getUserEntity());
   }

   @Test
    void getUserByIdTest() throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException {
       UserEntity userEntity = new UserEntity("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
       when(userRepository.getUserById("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userEntity);
       UserEntity userEntity1 = userServiceImpl.getUserById("73578dfd-e7c9-4381-a348-113e72d80fa2");
       Assertions.assertEquals(userEntity.getUserId(),userEntity1.getUserId());
   }


    @Test
    void getUserByNameTest() throws UserExceptionMessage {
        UserEntity userEntity = new UserEntity("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
        List<UserEntity> userEntities = new ArrayList<>();
        userEntities.add(userEntity);
        when(userRepository.findByNameIgnoreCase(userEntity.getUserName())).thenReturn(userEntities);
        List<UserEntity> userEntities1 = userServiceImpl.getUserByName(userEntity.getUserName());
        Assertions.assertEquals(userEntities.get(0).getUserName(),userEntities1.get(0).getUserName());

    }

    @Test
    void getUserByEmail() throws UserExceptionMessage {
        UserEntity userEntity = new UserEntity("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
        when(userRepository.findByMail("vinay@gmail.com")).thenReturn(userEntity);
        UserEntity user = userServiceImpl.getUserByEmail(userEntity.getEmail());
        Assertions.assertEquals(userEntity.getUserId(),user.getUserId());
    }

    @Test
    void sendUserMedicinesTest(){

    }
}
