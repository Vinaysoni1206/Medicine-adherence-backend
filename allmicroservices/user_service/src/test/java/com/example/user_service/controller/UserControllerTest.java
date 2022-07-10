package com.example.user_service.controller;

import com.example.user_service.model.user.UserEntity;
import com.example.user_service.pojos.dto.LoginDTO;
import com.example.user_service.pojos.dto.user.UserDetailEntityDTO;
import com.example.user_service.pojos.dto.user.UserEntityDTO;
import com.example.user_service.pojos.dto.user.UserMailDTO;
import com.example.user_service.pojos.dto.user.UserMedicineDTO;
import com.example.user_service.pojos.response.user.UserDetailResponsePage;
import com.example.user_service.pojos.response.user.UserMailResponse;
import com.example.user_service.pojos.response.user.UserProfileResponse;
import com.example.user_service.pojos.response.user.UserResponse;
import com.example.user_service.service.user.UserService;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

    @Mock
    RabbitTemplate rabbitTemplate;
    @Mock
    JwtUtil jwtUtil;

    @BeforeEach
    public void setUp(){
        this.mockMvc= MockMvcBuilders.standaloneSetup(userController).build();
    }
    UserEntity userEntity = new UserEntity("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
    UserEntityDTO userEntityDTO= new UserEntityDTO("vinay","vinay@gmail.com");
    UserResponse userResponse= new UserResponse();
    UserDetailEntityDTO userDetailEntityDTO = new UserDetailEntityDTO("nikunj","vinay@gmail.com","something",21,null,"Male","AB+","UnMarried",60);
    UserMedicineDTO userMedicineDTO= new UserMedicineDTO();
    List<UserMedicineDTO> userMedicineDTOList= new ArrayList<>();
    UserProfileResponse userProfileResponse= new UserProfileResponse();
    UserMailResponse userMailResponse= new UserMailResponse();
    UserDetailResponsePage userDetailResponsePage= new UserDetailResponsePage();


    @Test
    @ExtendWith(MockitoExtension.class)
    void saveUser() throws Exception {
        Mockito.when(userService.saveUser(any(),anyString(),anyString())).thenReturn(userResponse);
        String jsonRequest=objectMapper.writeValueAsString(userEntityDTO);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/user?fcmToken=yfdyiuwafviy&picPath=gedfigiagf")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void refreshToken() throws Exception {
        Mockito.when(userService.getUserById(anyString())).thenReturn(userEntity);
        Mockito.when(jwtUtil.generateToken(anyString())).thenReturn("jshfjdhfksj");
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/refreshToken?uid=3549759519459734975")
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
        Mockito.when(userService.getUserById1(anyString())).thenReturn(userDetailEntityDTO);
        Mockito.when(userService.getUserMedicineById(anyString())).thenReturn(userMedicineDTOList);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/user?userId=69216495194519259")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void getUserByEmail() throws Exception{
        UserMailDTO userMailDTO= new UserMailDTO();
        Mockito.when(userService.getUserByEmail1(anyString())).thenReturn(userMailDTO);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/email?email=vinay@gmail.com&sender=vinay")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void getUserByEmailException() throws Exception{
        UserMailDTO userMailDTO= new UserMailDTO();
        Mockito.when(userService.getUserByEmail1(anyString())).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/email?email=vinay@gmail.com&sender=vinay")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }


    @Test
    @ExtendWith(MockitoExtension.class)
    void sendPdf() throws Exception{
        Mockito.when(userService.sendUserMedicines(123)).thenReturn("dydytkgjvhviyfoutyrxuljh");
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/pdf?medId=123")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

    }
}