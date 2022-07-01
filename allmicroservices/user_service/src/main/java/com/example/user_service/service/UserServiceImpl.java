package com.example.user_service.service;


import com.example.user_service.config.PdfMailSender;
import com.example.user_service.exception.DataAccessExceptionMessage;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.model.medicine.MedicineHistory;
import com.example.user_service.model.user.UserDetails;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.pojos.dto.user.UserDetailEntityDTO;
import com.example.user_service.pojos.dto.user.UserEntityDTO;
import com.example.user_service.pojos.dto.user.UserMailDTO;
import com.example.user_service.pojos.dto.user.UserMedicineDTO;
import com.example.user_service.pojos.response.UserResponse;
import com.example.user_service.pojos.response.user.UserDetailResponsePage;
import com.example.user_service.repository.UserDetailsRepository;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.util.Datehelper;

import com.example.user_service.util.JwtUtil;
import com.example.user_service.util.Messages;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private UserDetailsRepository userDetailsRepository;
    private ModelMapper mapper;
    private PdfMailSender pdfMailSender;
    UserMedicineRepository userMedicineRepository;
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    UserServiceImpl( UserRepository userRepository,JwtUtil jwtUtil ,UserDetailsRepository userDetailsRepository,ModelMapper mapper, PdfMailSender pdfMailSender, PasswordEncoder passwordEncoder,UserMedicineRepository userMedicineRepository ){
        this.userRepository= userRepository;
        this.userDetailsRepository= userDetailsRepository;
        this.userMedicineRepository = userMedicineRepository;
        this.mapper= mapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.pdfMailSender = pdfMailSender;
    }
public UserServiceImpl(UserRepository userRepository2, UserMedicineRepository userMedicineRepository1){
        this.userRepository= userRepository2;
        this.userMedicineRepository= userMedicineRepository1;
    }
    @Override
    public UserResponse saveUser(UserEntityDTO userEntityDTO, String fcmToken, String picPath) throws UserExceptionMessage {

        try {
            UserEntity user = getUserByEmail(userEntityDTO.getEmail());
            if (user != null) {
                return new UserResponse(Messages.FAILED, "User is already present", new ArrayList<>(Arrays.asList(user)), "", "");
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
                throw new UserExceptionMessage("Error try again!");

            }
            String jwtToken = jwtUtil.generateToken(ue.getUserName());
            String refreshToken = passwordEncoder.encode(ue.getUserId());

            return new UserResponse(Messages.SUCCESS, "Saved user successfully", new ArrayList<>(Arrays.asList(ue)), jwtToken, refreshToken);
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }
    }

    @Override
    @Async
    public CompletableFuture<UserDetailResponsePage> getUsers(int pageNo, int pageSize) {
        try {
            Pageable paging = PageRequest.of(pageNo,pageSize);
            Page<UserDetailEntityDTO> userEntityPage = userRepository.findAllUsers(paging);
//            List<UserDetailEntityDTO> userEntityDTO = userEntityPage.stream().map(p -> new UserDetailEntityDTO(p.getUserName(),p.getEmail(),p.getUserDetails().getBio(),p.getUserDetails().getAge(),p.getUserDetails().getUserContact(),p.getUserDetails().getGender(),p.getUserDetails().getBloodGroup(),p.getUserDetails().getMaritalStatus(),p.getUserDetails().getWeight())).collect(Collectors.toList());
            logger.info(Thread.currentThread().getName());
            return  CompletableFuture.completedFuture(new UserDetailResponsePage(Messages.SUCCESS,Messages.DATA_FOUND,userEntityPage.getTotalElements(),userEntityPage.getTotalPages(),pageNo,userEntityPage.getContent()));
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }
    }


    @Override
    public UserEntity getUserById(String userId) throws UserExceptionMessage {
        try {
            Optional<UserEntity> optionalUserEntity = Optional.ofNullable(userRepository.getUserById(userId));
            logger.info(Thread.currentThread().getName());
            if (optionalUserEntity.isEmpty()) {
                throw new UserExceptionMessage(Messages.DATA_NOT_FOUND);
            }

            return optionalUserEntity.get();
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }
    }


    @Override
    public UserEntity updateUser(String userId, UserEntityDTO userEntityDTO) {
        try {
            UserEntity userDB = userRepository.getUserById(userId);
            UserEntity userEntity = mapToEntity(userEntityDTO);
            if (Objects.nonNull(userEntity.getUserName()) && !"".equalsIgnoreCase(userEntity.getUserName())) {
                userDB.setUserName(userEntity.getUserName());
            }
            if (Objects.nonNull(userEntity.getEmail()) && !"".equalsIgnoreCase(userEntity.getEmail())) {
                userDB.setEmail(userEntity.getEmail());
            }

            return userRepository.save(userDB);
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }
    }

    @Override
    public List<UserEntity> getUserByName(String userName) throws UserExceptionMessage, NullPointerException {

        try {
            List<UserEntity> userEntity = userRepository.findByNameIgnoreCase(userName);
            if (userEntity.isEmpty()) {
                throw new UserExceptionMessage(Messages.DATA_NOT_FOUND);
            }
            return userEntity;
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }

    }

    @Override
    public UserMailDTO getUserByEmail1(String email) {
        try {
            return userRepository.findByMail1(email);
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }
    }

    @Override
    public UserEntity getUserByEmail(String email) {

        try {
            return userRepository.findByMail(email);
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }

    }


    @Override
    public String sendUserMedicines(Integer medId){
        try {
            Optional<UserMedicines> userMedicines = userMedicineRepository.findById(medId);
            if (userMedicines.isEmpty()) {
                return "Failed";
            }
            UserEntity entity = userMedicines.get().getUserEntity();
            List<MedicineHistory> medicineHistories = userMedicines.get().getMedicineHistories();
            return pdfMailSender.send(entity, userMedicines.get(), medicineHistories);
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }
    }

    @Override
    public UserResponse login(String mail, String fcmToken) throws UserExceptionMessage {
        try {
            UserEntity user = getUserByEmail(mail);
            UserDetails userDetails = user.getUserDetails();
            userDetails.setFcmToken(fcmToken);
            userDetailsRepository.save(userDetails);
            user = getUserByEmail(mail);
            if (user != null) {
                String jwtToken = jwtUtil.generateToken(user.getUserName());
                String refreshToken = passwordEncoder.encode(user.getUserId());
                return new UserResponse(Messages.SUCCESS, "Success", new ArrayList<>(Arrays.asList(user)), jwtToken, refreshToken);
            }
            throw new UserExceptionMessage(Messages.DATA_NOT_FOUND);

        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }
        catch (NullPointerException e){
            throw new UserExceptionMessage(Messages.DATA_NOT_FOUND);

        }


    }

    @Override
    public UserDetailEntityDTO getUserById1(String userId) throws UserExceptionMessage {
        try {
            Optional<UserDetailEntityDTO> optionalUserEntity = Optional.ofNullable(userRepository.getUserById1(userId));
            logger.info(Thread.currentThread().getName());
            if (optionalUserEntity.isEmpty()) {
                throw new UserExceptionMessage(Messages.DATA_NOT_FOUND);
            }

            return optionalUserEntity.get();
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }
    }

    @Override
    public List<UserMedicineDTO> getUserMedicineById(String userId) throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException {
        try {
            Optional<List<UserMedicineDTO>> optionalUserEntity = Optional.ofNullable(userRepository.getUserMedicineById(userId));
            logger.info(Thread.currentThread().getName());
            if (optionalUserEntity.isEmpty()) {
                throw new UserExceptionMessage(Messages.DATA_NOT_FOUND);
            }

            return optionalUserEntity.get();
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }
    }

    private UserEntity mapToEntity(UserEntityDTO userEntityDTO) {
        return mapper.map(userEntityDTO, UserEntity.class);
    }
}
