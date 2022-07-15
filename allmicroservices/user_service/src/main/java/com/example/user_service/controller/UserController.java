package com.example.user_service.controller;


import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.pojos.MailInfo;
import com.example.user_service.pojos.dto.LoginDTO;
import com.example.user_service.pojos.dto.user.UserDetailEntityDTO;
import com.example.user_service.pojos.dto.user.UserEntityDTO;
import com.example.user_service.pojos.dto.user.UserMailDTO;
import com.example.user_service.pojos.dto.user.UserMedicineDTO;
import com.example.user_service.pojos.response.user.UserProfileResponse;
import com.example.user_service.pojos.response.user.UserResponse;
import com.example.user_service.pojos.response.user.UserDetailResponsePage;
import com.example.user_service.pojos.response.user.UserMailResponse;
import com.example.user_service.service.user.UserService;
import com.example.user_service.util.JwtUtil;
import com.example.user_service.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping(path = "/api/v1")
@Validated
public class UserController {
    private final UserService userService;

    private final RabbitTemplate rabbitTemplate;
    private final JwtUtil jwtUtil;

    UserController(UserService userService,RabbitTemplate rabbitTemplate,JwtUtil jwtUtil){
        this.userService= userService;
        this.rabbitTemplate=rabbitTemplate;
        this.jwtUtil=jwtUtil;
    }

    private final Logger logger= LoggerFactory.getLogger(UserController.class);
    // saving the user when they signup
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @PostMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> saveUser(@NotBlank @NotNull @RequestParam(name = "fcmToken") String fcmToken, @NotBlank @NotNull @RequestParam(name = "picPath") String picPath, @Valid @RequestBody UserEntityDTO userEntityDTO) throws UserExceptionMessage{
        logger.info("Saving User : {}",userEntityDTO);
        return new ResponseEntity<>(userService.saveUser(userEntityDTO, fcmToken, picPath), HttpStatus.CREATED);


    }

    @GetMapping(value = "/refreshToken",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> refreshToken(@NotBlank @NotNull @RequestParam(name = "uid") String uid) throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException {
        logger.info("Generating refresh token");
        String jwtToken = jwtUtil.generateToken(userService.getUserById(uid).getUserName());
        return new ResponseEntity<>(jwtToken, HttpStatus.CREATED);

    }

    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginDTO loginDTO) throws UserExceptionMessage {
        logger.info("Loging the user : {}",loginDTO);
        return new ResponseEntity<>(userService.login(loginDTO.getEmail(),loginDTO.getFcmToken()), HttpStatus.OK);


    }


//     fetching all the users along with details
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    public ResponseEntity<UserDetailResponsePage> getUsers(@RequestParam(defaultValue = "1") int pageNo,
                                                           @RequestParam(defaultValue = "3") int pageSize) throws UserExceptionMessage, ExecutionException, InterruptedException {

        logger.info("Fetching all users");
        return new ResponseEntity<>(userService.getUsers(pageNo,pageSize).get(), HttpStatus.OK);


    }

    // fetching user by id
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfileResponse> getUserById(@NotBlank @NotNull @RequestParam("userId") String userId) throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException {

        logger.info("Fetching user by Id : {}",userId);
        UserDetailEntityDTO user = userService.getUserById1(userId);
        List<UserMedicineDTO> list = userService.getUserMedicineById(userId);
        UserProfileResponse userProfileResponse = new UserProfileResponse(Messages.SUCCESS,Messages.DATA_FOUND, user, list);
        return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);


    }

    // fetching the user with email if not present then sending to that email address
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserMailResponse> getUserByEmail(@NotBlank @NotNull @RequestParam("email") String email
            , @RequestParam("sender") String sender)
            throws UserExceptionMessage {
        logger.info("Fetching a user by mail : {}",email);
        UserMailDTO userEntity = userService.getUserByEmail1(email);
        if (userEntity == null) {
            logger.info("Sending mail if email is not present : {}",email);
            rabbitTemplate.convertAndSend("project_exchange",
                    "mail_key", new MailInfo(email, "Please join", "patient_request", sender));
            return new ResponseEntity<>(new UserMailResponse(Messages.DATA_NOT_FOUND,"Invitation sent to user with given email id!", null),HttpStatus.OK);

        }
        return new ResponseEntity<>(new UserMailResponse(Messages.SUCCESS,Messages.DATA_FOUND,userEntity),HttpStatus.OK);

    }


    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/pdf",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> sendPdf(@NotBlank @NotNull @RequestParam(name = "medId") Integer medId) throws IOException, MessagingException, UserExceptionMessage {
        logger.info("Generating PDF file for a particular medId :{}",medId);
        String filePath = userService.sendUserMedicines(medId);
        UserResponse userResponse = new UserResponse(Messages.SUCCESS, filePath, null, "", "");
        return new ResponseEntity<>(userResponse, HttpStatus.OK);

    }


}
