package com.example.user_service.controller;


import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.pojos.response.*;
import com.example.user_service.pojos.request.LoginDTO;
import com.example.user_service.pojos.request.UserDetailEntityDTO;
import com.example.user_service.pojos.request.UserDTO;
import com.example.user_service.pojos.request.UserMailDTO;
import com.example.user_service.pojos.request.UserMedicineDTO;
import com.example.user_service.service.UserService;
import com.example.user_service.util.JwtUtil;
import com.example.user_service.util.Constants;
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
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;


/**
 * This controller is used to create restful web services for user
 */
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

    /**
     * Saves the user when they sign up
     */
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @PostMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> saveUser(@NotBlank @NotNull @RequestParam(name = "fcmToken") String fcmToken, @NotBlank @NotNull @RequestParam(name = "picPath") String picPath, @Valid @RequestBody UserDTO userDTO) throws UserExceptionMessage{
        logger.info("Saving User : {}", userDTO);
        return new ResponseEntity<>(userService.saveUser(userDTO, fcmToken, picPath), HttpStatus.CREATED);


    }

    /**
     * This allows you to generate a new jwt token after validating with your refresh token
     */
    @GetMapping(value = "/refreshToken",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RefreshTokenResponse> getRefreshToken(HttpServletRequest httpServletRequest) throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException {
        return new ResponseEntity<>(userService.getRefreshToken(httpServletRequest),HttpStatus.OK);
    }

    /**
     * Logins the user when they want to Add a caretaker
     */
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginDTO loginDTO) throws UserExceptionMessage {
        logger.info("Loging the user : {}",loginDTO);
        return new ResponseEntity<>(userService.login(loginDTO.getEmail(),loginDTO.getFcmToken()), HttpStatus.OK);


    }


    /**
     * Fetching all the users along with details
     */
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    public ResponseEntity<UserDetailResponsePage> getUsers(@RequestParam(defaultValue = "1") int pageNo,
                                                           @RequestParam(defaultValue = "3") int pageSize) throws UserExceptionMessage, ExecutionException, InterruptedException {

        logger.info("Fetching all users");
        return new ResponseEntity<>(userService.getUsers(pageNo,pageSize).get(), HttpStatus.OK);


    }


    /**
     * Fetching user by id along with list of medicines
     */
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfileResponse> getUserById(@NotBlank @NotNull @RequestParam("userId") String userId) throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException {

        logger.info("Fetching user by Id : {}",userId);
        UserDetailEntityDTO user = userService.getUserById1(userId);
        List<UserMedicineDTO> list = userService.getUserMedicineById(userId);
        UserProfileResponse userProfileResponse = new UserProfileResponse(Constants.SUCCESS, Constants.DATA_FOUND, user, list);
        return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);


    }


    /**
     * Fetching the user with email if not present then sending invitation to that email address
     */
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
            return new ResponseEntity<>(new UserMailResponse(Constants.DATA_NOT_FOUND,"Invitation sent to user with given email id!", null),HttpStatus.OK);

        }
        return new ResponseEntity<>(new UserMailResponse(Constants.SUCCESS, Constants.DATA_FOUND,userEntity),HttpStatus.OK);

    }

    /**
     * Generates a pdf for the adherence maintained by a user
     */
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/pdf",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> sendPdf(@NotBlank @NotNull @RequestParam(name = "medId") Integer medId) throws IOException, MessagingException, UserExceptionMessage {
        logger.info("Generating PDF file for a particular medId :{}",medId);
        String filePath = userService.sendUserMedicines(medId);
        UserResponse userResponse = new UserResponse(Constants.SUCCESS, filePath, null, "", "");
        return new ResponseEntity<>(userResponse, HttpStatus.OK);

    }


}
