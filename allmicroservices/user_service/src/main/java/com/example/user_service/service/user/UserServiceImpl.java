package com.example.user_service.service.user;


import com.example.user_service.config.PdfMailSender;
import com.example.user_service.exception.user.UserExceptionMessage;
import com.example.user_service.model.medicine.MedicineHistory;
import com.example.user_service.model.user.UserDetails;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.pojos.dto.user.UserDetailEntityDTO;
import com.example.user_service.pojos.dto.user.UserEntityDTO;
import com.example.user_service.pojos.dto.user.UserMailDTO;
import com.example.user_service.pojos.dto.user.UserMedicineDTO;
import com.example.user_service.pojos.response.user.UserResponse;
import com.example.user_service.pojos.response.user.UserDetailResponsePage;
import com.example.user_service.repository.UserDetailsRepository;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.util.Datehelper;

import com.example.user_service.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.example.user_service.util.Messages.*;
import static com.example.user_service.util.Messages.LoggerConstants.*;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final ModelMapper mapper;
    private final PdfMailSender pdfMailSender;
    UserMedicineRepository userMedicineRepository;
    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, UserDetailsRepository userDetailsRepository, ModelMapper mapper, PdfMailSender pdfMailSender, PasswordEncoder passwordEncoder, UserMedicineRepository userMedicineRepository){
        this.userRepository= userRepository;
        this.userDetailsRepository= userDetailsRepository;
        this.userMedicineRepository = userMedicineRepository;
        this.mapper= mapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.pdfMailSender = pdfMailSender;

    }


    @Override
    public UserResponse saveUser(UserEntityDTO userEntityDTO, String fcmToken, String picPath) throws UserExceptionMessage {
            logger.info(STARTING_METHOD_EXECUTION);
            UserEntity user = getUserByEmail(userEntityDTO.getEmail());
            if (user != null) {
                return new UserResponse(FAILED, USER_ALREADY_PRESENT, new ArrayList<>(Arrays.asList(user)), "", "");
            }
            logger.info(Thread.currentThread().getName());
            UserEntity userEntity = mapToEntity(userEntityDTO);
            userEntity.setLastLogin(Datehelper.getcurrentdatatime());
            userEntity.setCreatedAt(Datehelper.getcurrentdatatime());
            UserEntity ue = userRepository.save(userEntity);
            UserDetails userDetails = new UserDetails();
            userDetails.setFcmToken(fcmToken);
            userDetails.setPicPath(picPath);
            userDetails.setUser(ue);
            userDetailsRepository.save(userDetails);

            if (ue.getUserName() == null) {
                throw new UserExceptionMessage(ERROR_TRY_AGAIN);

            }
            String jwtToken = jwtUtil.generateToken(ue.getUserName());
            String refreshToken = passwordEncoder.encode(ue.getUserId());
        logger.info(EXITING_METHOD_EXECUTION);
        return new UserResponse(SUCCESS, "Saved user successfully", new ArrayList<>(Arrays.asList(ue)), jwtToken, refreshToken);

    }

    @Override
    @Async
    public CompletableFuture<UserDetailResponsePage> getUsers(int pageNo, int pageSize) throws UserExceptionMessage {
        logger.info(STARTING_METHOD_EXECUTION);
        Pageable paging = PageRequest.of(pageNo,pageSize);
               Page<UserDetailEntityDTO> userEntityPage = userRepository.findAllUsers(paging);
               if(userEntityPage.isEmpty()){
                   throw new UserExceptionMessage(DATA_NOT_FOUND);
               }
               logger.info(Thread.currentThread().getName());
        logger.info(EXITING_METHOD_EXECUTION);
        return  CompletableFuture.completedFuture(new UserDetailResponsePage(SUCCESS,DATA_FOUND,userEntityPage.getTotalElements(),userEntityPage.getTotalPages(),pageNo,userEntityPage.getContent()));



    }


    @Override
    public UserEntity getUserById(String userId) throws UserExceptionMessage {
        logger.info(STARTING_METHOD_EXECUTION);
            Optional<UserEntity> optionalUserEntity = Optional.ofNullable(userRepository.getUserById(userId));
            logger.info(Thread.currentThread().getName());
            if (optionalUserEntity.isEmpty()) {
                throw new UserExceptionMessage(DATA_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return optionalUserEntity.get();
    }


    @Override
    public List<UserEntity> getUserByName(String userName) throws UserExceptionMessage, NullPointerException {

        logger.info(STARTING_METHOD_EXECUTION);
        List<UserEntity> userEntity = userRepository.findByNameIgnoreCase(userName);
            if (userEntity.isEmpty()) {
                throw new UserExceptionMessage(DATA_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return userEntity;


    }

    @Override
    public UserMailDTO getUserByEmail1(String email) throws  UserExceptionMessage{

        logger.info(STARTING_METHOD_EXECUTION);
        UserMailDTO userMailDTO=userRepository.findByMail1(email);
           if(userMailDTO.getUserName()==null) {
               throw new UserExceptionMessage(DATA_NOT_FOUND);
           }
        logger.info(EXITING_METHOD_EXECUTION);
        return userMailDTO;
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return userRepository.findByMail(email);
    }


    @Override
    public String sendUserMedicines(Integer medId) throws UserExceptionMessage, FileNotFoundException {

        logger.info(STARTING_METHOD_EXECUTION);
        Optional<UserMedicines> userMedicines = userMedicineRepository.findById(medId);
            if (userMedicines.isEmpty()) {
                throw new UserExceptionMessage(MEDICINE_NOT_FOUND);
            }
            UserEntity entity = userMedicines.get().getUserEntity();
            List<MedicineHistory> medicineHistories = userMedicines.get().getMedicineHistories();

        logger.info(EXITING_METHOD_EXECUTION);
        return pdfMailSender.send(entity, userMedicines.get(), medicineHistories);
    }

    @Override
    public UserResponse login(String mail, String fcmToken) throws UserExceptionMessage {

        logger.info(STARTING_METHOD_EXECUTION);
        UserEntity user = getUserByEmail(mail);
            UserDetails userDetails = user.getUserDetails();
            userDetails.setFcmToken(fcmToken);
            userDetailsRepository.save(userDetails);
            user = getUserByEmail(mail);
            if (user.getUserName() != null) {
                String jwtToken = jwtUtil.generateToken(user.getUserName());
                String refreshToken = passwordEncoder.encode(user.getUserId());
                logger.info(EXITING_METHOD_EXECUTION);
                return new UserResponse(SUCCESS, "Success", new ArrayList<>(Arrays.asList(user)), jwtToken, refreshToken);
            }
            throw new UserExceptionMessage(DATA_NOT_FOUND);
    }

    @Override
    public UserDetailEntityDTO getUserById1(String userId) throws UserExceptionMessage {

        logger.info(STARTING_METHOD_EXECUTION);
        Optional<UserDetailEntityDTO> optionalUserEntity = Optional.ofNullable(userRepository.getUserById1(userId));
            logger.info(Thread.currentThread().getName());
            if (optionalUserEntity.isEmpty()) {
                throw new UserExceptionMessage(DATA_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return optionalUserEntity.get();
    }

    @Override
    public List<UserMedicineDTO> getUserMedicineById(String userId) throws UserExceptionMessage{
        logger.info(STARTING_METHOD_EXECUTION);
        Optional<List<UserMedicineDTO>> optionalUserEntity = Optional.ofNullable(userRepository.getUserMedicineById(userId));
            logger.info(Thread.currentThread().getName());
            if (optionalUserEntity.isEmpty()) {
                throw new UserExceptionMessage(DATA_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return optionalUserEntity.get();

    }

    private UserEntity mapToEntity(UserEntityDTO userEntityDTO) {
        return mapper.map(userEntityDTO, UserEntity.class);
    }
}