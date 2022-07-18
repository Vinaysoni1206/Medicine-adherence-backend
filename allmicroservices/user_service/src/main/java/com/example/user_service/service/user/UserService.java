package com.example.user_service.service.user;

import com.example.user_service.exception.user.UserExceptionMessage;
import com.example.user_service.exception.medicine.UserMedicineException;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.pojos.dto.user.UserDetailEntityDTO;
import com.example.user_service.pojos.dto.user.UserEntityDTO;
import com.example.user_service.pojos.dto.user.UserMailDTO;
import com.example.user_service.pojos.dto.user.UserMedicineDTO;
import com.example.user_service.pojos.response.user.UserResponse;
import com.example.user_service.pojos.response.user.UserDetailResponsePage;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public interface UserService {

     UserResponse saveUser(UserEntityDTO userEntityDTO, String fcmToken, String picPath) throws UserExceptionMessage;

     CompletableFuture<UserDetailResponsePage> getUsers(int pageNo, int pageSize) throws UserExceptionMessage;

     UserEntity getUserById(String userId) throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException;

     List<UserEntity> getUserByName(String userName)throws UserExceptionMessage;

     UserMailDTO getUserByEmail1(String email) throws UserExceptionMessage;

     UserEntity getUserByEmail(String email) throws UserExceptionMessage;

     String sendUserMedicines(Integer userId) throws MessagingException, IOException, UserExceptionMessage;

     UserResponse login(String mail , String fcmToken) throws UserExceptionMessage;

     UserDetailEntityDTO getUserById1(String userId) throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException;

     List<UserMedicineDTO> getUserMedicineById(String userId) throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException;
}
