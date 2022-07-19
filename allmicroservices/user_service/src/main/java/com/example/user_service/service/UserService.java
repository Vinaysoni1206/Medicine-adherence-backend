package com.example.user_service.service;

import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.model.User;
import com.example.user_service.pojos.request.UserDetailEntityDTO;
import com.example.user_service.pojos.request.UserDTO;
import com.example.user_service.pojos.request.UserMailDTO;
import com.example.user_service.pojos.request.UserMedicineDTO;
import com.example.user_service.pojos.response.RefreshTokenResponse;
import com.example.user_service.pojos.response.UserResponse;
import com.example.user_service.pojos.response.UserDetailResponsePage;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * This is an interface for user service
 */
public interface UserService {

     UserResponse saveUser(UserDTO userDTO, String fcmToken, String picPath) throws UserExceptionMessage;

     CompletableFuture<UserDetailResponsePage> getUsers(int pageNo, int pageSize) throws UserExceptionMessage;

     User getUserById(String userId) throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException;

     List<User> getUserByName(String userName)throws UserExceptionMessage;

     UserMailDTO getUserByEmail1(String email) throws UserExceptionMessage;

     User getUserByEmail(String email) throws UserExceptionMessage;

     String sendUserMedicines(Integer userId) throws MessagingException, IOException, UserExceptionMessage;

     UserResponse login(String mail , String fcmToken) throws UserExceptionMessage;

     UserDetailEntityDTO getUserById1(String userId) throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException;

     List<UserMedicineDTO> getUserMedicineById(String userId) throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException;

     RefreshTokenResponse getRefreshToken(HttpServletRequest token) throws UserExceptionMessage;
}
