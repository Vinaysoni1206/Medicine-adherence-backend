package com.example.user_service.service.impl;


import com.example.user_service.config.PdfMailSender;
import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.MedicineHistory;
import com.example.user_service.model.UserDetails;
import com.example.user_service.model.User;
import com.example.user_service.model.UserMedicines;
import com.example.user_service.pojos.request.UserDetailEntityDTO;
import com.example.user_service.pojos.request.UserDTO;
import com.example.user_service.pojos.request.UserMedicineDTO;
import com.example.user_service.pojos.response.RefreshTokenResponse;
import com.example.user_service.pojos.response.UserResponse;
import com.example.user_service.pojos.response.UserDetailResponsePage;
import com.example.user_service.repository.UserDetailsRepository;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserService;
import com.example.user_service.util.Datehelper;

import com.example.user_service.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.example.user_service.util.Constants.*;
import static com.example.user_service.util.Constants.LoggerConstants.*;


/**
 * This class contains all the business logic for the User controller
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final ModelMapper mapper;
    private final PdfMailSender pdfMailSender;
    private final UserMedicineRepository userMedicineRepository;
    private final JwtUtil jwtUtil;
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserResponse saveUser(UserDTO userDTO, String fcmToken, String picPath) throws ResourceNotFoundException, UserExceptionMessage {
            logger.info(STARTING_METHOD_EXECUTION);
            logger.info("Saving user with email: {}",userDTO.getEmail());
            User user = getUserByEmail(userDTO.getEmail());
            if (user != null) {
                return new UserResponse(FAILED, USER_ALREADY_PRESENT, new ArrayList<>(Arrays.asList(user)), "", "");
            }
            logger.info(Thread.currentThread().getName());
            User userEntity = mapToEntity(userDTO);
            userEntity.setLastLogin(Datehelper.getCurrentDataTime());
            userEntity.setCreatedAt(Datehelper.getCurrentDataTime());
            UserDetails userDetails = new UserDetails();
            userDetails.setFcmToken(fcmToken);
            userDetails.setPicPath(picPath);
            userDetails.setUser(userEntity);
            User ue= userRepository.save(userEntity);

            if (ue.getUserName() == null) {
                logger.debug("User not saved");
                throw new ResourceNotFoundException(USER_NOT_SAVED);

            }
            String jwtToken = jwtUtil.generateToken(ue.getUserId());
            String refreshToken = jwtUtil.generateRefreshToken(ue.getUserId()) ;
        logger.info(EXITING_METHOD_EXECUTION);
        logger.info(SAVED_USER);
        return new UserResponse(SUCCESS, SAVED_USER, new ArrayList<>(Arrays.asList(ue)), jwtToken, refreshToken);

    }

    @Override
    @Async
    public CompletableFuture<UserDetailResponsePage> getUsers(int pageNo, int pageSize) throws ResourceNotFoundException {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Fetching all users");
        Pageable paging = PageRequest.of(pageNo,pageSize);
               Page<UserDetailEntityDTO> userEntityPage = userRepository.findAllUsers(paging);
               if(userEntityPage.isEmpty()){
                   logger.debug(NO_USERS_FOUND);
                   throw new ResourceNotFoundException(NO_USERS_FOUND);
               }
               logger.info(Thread.currentThread().getName());
        logger.info(EXITING_METHOD_EXECUTION);
        logger.info("Fetched all users");
        return  CompletableFuture.completedFuture(new UserDetailResponsePage(SUCCESS,DATA_FOUND,userEntityPage.getTotalElements(),userEntityPage.getTotalPages(),pageNo,userEntityPage.getContent()));
    }


    @Override
    public User getUserById(String userId) throws ResourceNotFoundException {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Fetching users by ID : {}",userId);
            Optional<User> optionalUserEntity = Optional.ofNullable(userRepository.getUserById(userId));
            logger.info(Thread.currentThread().getName());
            if (optionalUserEntity.isEmpty()) {
                logger.debug(USER_ID_NOT_FOUND);
                throw new ResourceNotFoundException(USER_ID_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return optionalUserEntity.get();
    }


    @Override
    public List<User> getUserByName(String userName) throws  NullPointerException, ResourceNotFoundException {

        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Searching for user by name : {}",userName);
        List<User> user = userRepository.findByNameIgnoreCase(userName);
            if (user.isEmpty()) {
                logger.debug(USER_NAME_NOT_FOUND);
                throw new ResourceNotFoundException(USER_NAME_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return user;


    }

    @Override
    public User getUserByEmailCustom(String email) throws ResourceNotFoundException {

        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Searching for user by email : {}",email);
        User userMailDTO=userRepository.findByMail(email);
           if(userMailDTO.getUserName()==null) {
               logger.debug(USER_EMAIL_NOT_FOUND);
               throw new ResourceNotFoundException(USER_EMAIL_NOT_FOUND);
           }
        logger.info(EXITING_METHOD_EXECUTION);
        return userMailDTO;
    }

    @Override
    public User getUserByEmail(String email) throws UserExceptionMessage {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        User user= userRepository.findByMail(email);
        if(user.getUserId()==null) {
            throw new UserExceptionMessage(DATA_NOT_FOUND);
        }else {
            return user;
        }
    }


    @Override
    public String sendUserMedicines(Integer medicineId) throws FileNotFoundException, ResourceNotFoundException {

        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Fetching user medicines by medicine id : {}",medicineId);
        Optional<UserMedicines> userMedicines = userMedicineRepository.findById(medicineId);
            if (userMedicines.isEmpty()) {
                logger.debug("Medicine list is empty : {}",MEDICINES_NOT_FOUND);
                throw new ResourceNotFoundException(MEDICINES_NOT_FOUND);
            }
            User entity = userMedicines.get().getUser();
            List<MedicineHistory> medicineHistories = userMedicines.get().getMedicineHistories();

        logger.info(EXITING_METHOD_EXECUTION);
        return pdfMailSender.send(entity, userMedicines.get(), medicineHistories);
    }

    @Override
    public UserResponse login(String mail, String fcmToken) throws UserExceptionMessage, ResourceNotFoundException {

        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("logging in with email: {}",mail);
        User user = getUserByEmail(mail);
            UserDetails userDetails = user.getUserDetails();
            userDetails.setFcmToken(fcmToken);
            userDetailsRepository.save(userDetails);
            user = getUserByEmail(mail);
            if (user.getUserName() != null) {
                String jwtToken = jwtUtil.generateToken(user.getUserId());
                String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());
                logger.info(EXITING_METHOD_EXECUTION);
                return new UserResponse(SUCCESS, "Success", new ArrayList<>(Arrays.asList(user)), jwtToken, refreshToken);
            }
            logger.debug("User not found with this email :{}",mail);
            throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    @Override
    public UserDetailEntityDTO getUserByIdCustom(String userId) throws UserExceptionMessage, ResourceNotFoundException {

        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Fetching user me Id : {}",userId);
        Optional<UserDetailEntityDTO> optionalUserEntity = Optional.ofNullable(userRepository.getUserByIdCustom(userId));
            logger.info(Thread.currentThread().getName());
            if (optionalUserEntity.isEmpty()) {
                logger.debug("User not found with id :{}",userId);
                throw new ResourceNotFoundException(USER_ID_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return optionalUserEntity.get();
    }

    @Override
    public List<UserMedicineDTO> getUserMedicineById(String userId) throws UserExceptionMessage{
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Fetching list of user medicines by user id: {}",userId);
        Optional<List<UserMedicineDTO>> optionalUserEntity = Optional.ofNullable(userRepository.getUserMedicineById(userId));
            logger.info(Thread.currentThread().getName());
            if (optionalUserEntity.isEmpty()) {
                logger.debug("Medicine list is empty");
                throw new UserExceptionMessage(NO_MEDICINE_PRESENT);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return optionalUserEntity.get();

    }

    @Override
    public RefreshTokenResponse getRefreshToken(HttpServletRequest token,String userId) throws UserExceptionMessage, ResourceNotFoundException {
          RefreshTokenResponse response = new RefreshTokenResponse();
          logger.debug("Generating new JWT token ");
          final String authHeader = token.getHeader("Authorization");
          String username = getUserById(userId).getUserName();
          String jwt = null;
          if (authHeader != null && authHeader.startsWith("Bearer ")) {
              jwt = authHeader.substring(7);
              if(Boolean.TRUE.equals(checkRefreshToken(jwt))){
                  logger.info("Valid refresh token, generating new jwt token");
                  username = jwtUtil.extractUsername(jwt);
                  response.setJwtToken(jwtUtil.generateToken(username));
              response.setRefreshToken(jwt.trim());
              }else{
                  logger.info("Generating both jwt and refresh token");
                  response.setJwtToken(jwtUtil.generateToken(username));
                  response.setRefreshToken(jwtUtil.generateRefreshToken(username));
              }
              response.setStatus(SUCCESS);
              return response;
          } else {
              throw new UserExceptionMessage(INVALID_REFRESH_TOKEN);
          }

    }

    public Boolean checkRefreshToken(String jwt) {
        try{
            logger.info("Validating refresh token");
        return jwtUtil.validateToken(jwt.trim(),null);
        }catch (ExpiredJwtException e){
            logger.info("Refresh token expired");
            return false;
        }
    }

    private User mapToEntity(UserDTO userDTO) {
        logger.info("Mapping DTO to entity");
        return mapper.map(userDTO, User.class);
    }
}