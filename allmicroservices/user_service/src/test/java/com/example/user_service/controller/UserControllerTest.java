package com.example.user_service.controller;

import com.example.user_service.model.User;
import com.example.user_service.pojos.request.LoginDTO;
import com.example.user_service.pojos.request.UserDetailEntityDTO;
import com.example.user_service.pojos.request.UserDTO;
import com.example.user_service.pojos.request.UserMailDTO;
import com.example.user_service.pojos.request.UserMedicineDTO;
import com.example.user_service.pojos.response.*;
import com.example.user_service.service.UserService;
import com.example.user_service.util.Constants;
import com.example.user_service.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
class UserControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper=new ObjectMapper();

    @InjectMocks
    private UserController userController;

    @Mock
    UserService userService;



    @BeforeEach
    public void setUp(){
        this.mockMvc= MockMvcBuilders.standaloneSetup(userController).build();
    }
    User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
    UserDTO userDTO = new UserDTO("vinay","vinay@gmail.com");
    UserMailResponse userMailResponse= new UserMailResponse(Constants.SUCCESS, Constants.DATA_FOUND, user);
    UserResponse userResponse= new UserResponse();
    UserDetailResponsePage userDetailResponsePage= new UserDetailResponsePage();
    UserProfileResponse userProfileResponse= new UserProfileResponse();
    RefreshTokenResponse response= new RefreshTokenResponse();
    @Test
    @ExtendWith(MockitoExtension.class)
    void saveUser() throws Exception {
        Mockito.when(userService.saveUser(any(),anyString(),anyString())).thenReturn(userResponse);
        String jsonRequest=objectMapper.writeValueAsString(userDTO);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/user?fcmToken=yfdyiuwafviy&picPath=gedfigiagf")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void refreshToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/refreshToken?userId=3549759519459734975")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void login() throws Exception {
        LoginDTO loginDTO= new LoginDTO("vinay@gmail.com","yfiyfiyfiyfufof");
        String jsonValue= objectMapper.writeValueAsString(loginDTO);
        Mockito.when(userService.login(loginDTO.getEmail(),loginDTO.getFcmToken())).thenReturn(userResponse);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonValue))
                .andExpect(status().isOk());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void getUsers() throws Exception {
            Mockito.when(userService.getUsers(anyInt(),anyInt())).thenReturn(CompletableFuture.completedFuture(userDetailResponsePage));
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users").content(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void getUserById() throws Exception {
        Mockito.when(userService.getUserByIdCustom(anyString())).thenReturn(userProfileResponse);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/user?userId=69216495194519259")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void getUserByEmail() throws Exception{
        Mockito.when(userService.getUserByEmailCustom(anyString(),anyString())).thenReturn(userMailResponse);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/email?email=vinay@gmail.com&sender=vinay")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void getUserByEmailException() throws Exception{
        Mockito.when(userService.getUserByEmailCustom(anyString(),anyString())).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/email?email=vinay@gmail.com&sender=vinay")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }


    @Test
    @ExtendWith(MockitoExtension.class)
    void sendPdf() throws Exception{
        Mockito.when(userService.sendUserMedicines(123)).thenReturn(userResponse);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/pdf?medicineId=123")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

    }
}