package com.example.user_service.service.user;

import com.example.user_service.config.PdfMailSender;
import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.model.MedicineHistory;
import com.example.user_service.model.UserMedicines;
import com.example.user_service.model.UserDetails;
import com.example.user_service.model.User;
import com.example.user_service.pojos.request.UserDetailEntityDTO;
import com.example.user_service.pojos.request.UserDTO;
import com.example.user_service.pojos.request.UserMailDTO;
import com.example.user_service.pojos.request.UserMedicineDTO;
import com.example.user_service.pojos.response.*;
import com.example.user_service.repository.UserDetailsRepository;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.impl.UserServiceImpl;
import com.example.user_service.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.example.user_service.util.Constants.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserServiceTest {
    @InjectMocks
    UserServiceImpl userServiceImpl;
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMedicineRepository userMedicineRepository;

    @Mock
    private UserDetailsRepository userDetailsRepository;
    @Mock
    private ModelMapper mapper;
    @Mock
    private PdfMailSender pdfMailSender;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    RabbitTemplate rabbitTemplate;
    @BeforeEach
    public void initcase() {
        userServiceImpl= new UserServiceImpl(userRepository,userDetailsRepository,mapper,pdfMailSender,userMedicineRepository,jwtUtil,rabbitTemplate);
    }

    UserDetails userDetails= new UserDetails();
    User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),userDetails,null);
    UserResponse userResponse= new UserResponse("Failed","User is already present",new ArrayList<>(Arrays.asList(user)),null,null);
    UserDTO userDTO = new UserDTO("vinay","vinay@gmail.com");
    UserDetailEntityDTO userEntity = new UserDetailEntityDTO("vinay","vinay@gmail.com","something",21,8721L,"Male","AB+","UnMarried",60);
    UserDetailEntityDTO userEntity1 = new UserDetailEntityDTO("yatin","vinay@gmail.com","something",21,8721L,"Male","AB+","UnMarried",60);
    UserDetailEntityDTO userEntity2 = new UserDetailEntityDTO("nikunj","vinay@gmail.com","something",21,8721L,"Male","AB+","UnMarried",60);
    List<UserDetailEntityDTO> users = new ArrayList<>();
    List<User> userEntities = new ArrayList<>();

    int pageSize = 4;
    int pageNo = 0;
    Pageable paging = PageRequest.of(pageNo,pageSize);
    MedicineHistory medicineHistory = new MedicineHistory(123,"2022-04-16T16:05:20.961Z","10:00 AM","8:00 PM",null);
    List<MedicineHistory> medicineHistoryList= new ArrayList<>();
    Optional<UserMedicines> userMedicines= Optional.of(new UserMedicines(12345, "2022-04-16T16:05:20.961Z", "PCM", "for headache", "Mon", "2022-04-16T16:05:20.961Z", "10:00 AM", "10 AM", 12, 12, user, medicineHistoryList, null));

    String fcmToken = "";
    @Test
    @DisplayName("Test for saving user successfully")
    void saveUserTest() throws UserExceptionMessage, ResourceNotFoundException {
        when(userServiceImpl.getUserByEmail(user.getEmail())).thenReturn(null);
        when(mapper.map(userDTO, User.class)).thenReturn(user);
        when(jwtUtil.generateToken(user.getUserId())).thenReturn("fiasfiugaojfbjkabfk");
        when(jwtUtil.generateRefreshToken(user.getUserId())).thenReturn("ujagfgouiaetfiugljgb");
        when(userRepository.save(user)).thenReturn(user);
        UserResponse userResponse1= userServiceImpl.saveUser(userDTO,"eafyigfiagf","sfhoshgouahgo");
        Assertions.assertEquals(userResponse.getUser().get(0).getUserName(),userResponse1.getUser().get(0).getUserName());
        Assertions.assertEquals(userResponse.getUser().get(0).getUserId(),userResponse1.getUser().get(0).getUserId());
        Assertions.assertEquals(userResponse.getUser().get(0).getEmail(),userResponse1.getUser().get(0).getEmail());

    }

    @Test
    @DisplayName("Test for saving user while user exists")
    void saveUserTestUserExist() throws UserExceptionMessage, ResourceNotFoundException {
        when(userServiceImpl.getUserByEmail(user.getEmail())).thenReturn(user);
        UserResponse userResponse1= userServiceImpl.saveUser(userDTO,"eafyigfiagf","sfhoshgouahgo");
        Assertions.assertEquals(userResponse.getUser().get(0).getUserName(),userResponse1.getUser().get(0).getUserName());
        Assertions.assertEquals(userResponse.getUser().get(0).getUserId(),userResponse1.getUser().get(0).getUserId());
        Assertions.assertEquals(userResponse.getUser().get(0).getEmail(),userResponse1.getUser().get(0).getEmail());

    }

    @Test
    @DisplayName("Test for saving user exception")
    void saveUserInnerException() throws UserExceptionMessage {
        user.setUserName(null);
        when(userServiceImpl.getUserByEmail(any())).thenReturn(null);
        when(mapper.map(userDTO, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        try{
            userServiceImpl.saveUser(userDTO,"eafyigfiagf","sfhoshgouahgo");
        }catch(ResourceNotFoundException message){
            Assertions.assertEquals(USER_NOT_SAVED,message.getMessage());
        }
    }


    @Test
    @DisplayName("Test for getting users list successfully")
    void getUsersTest() throws Exception{
        users.add(userEntity);
        users.add(userEntity1);
        users.add(userEntity2);
        Page<UserDetailEntityDTO> userEntityPage= new PageImpl<>(users);
        when(userRepository.findAllUsers(paging)).thenReturn(userEntityPage);
        CompletableFuture<UserDetailResponsePage> userResponsePage1=userServiceImpl.getUsers(0,4);
        Assertions.assertEquals(3,userResponsePage1.get().getUsers().size());
        Assertions.assertEquals(userEntityPage.get().collect(Collectors.toList()).get(0).getUserName(),userResponsePage1.get().getUsers().get(0).getUserName() );
        Assertions.assertEquals(userEntityPage.get().collect(Collectors.toList()).get(0).getEmail(),userResponsePage1.get().getUsers().get(0).getEmail());
        Assertions.assertEquals(userEntityPage.get().collect(Collectors.toList()).get(0).getBio(),userResponsePage1.get().getUsers().get(0).getBio() );
        Assertions.assertEquals(userEntityPage.get().collect(Collectors.toList()).get(0).getGender(),userResponsePage1.get().getUsers().get(0).getGender() );
        Assertions.assertEquals(userEntityPage.get().collect(Collectors.toList()).get(0).getAge(),userResponsePage1.get().getUsers().get(0).getAge());
    }

    @Test
    @DisplayName("Test for getting users failed")
    void getUsersTestException(){
        Page<UserDetailEntityDTO> userEntityPage= new PageImpl<>(users);
        when(userRepository.findAllUsers(paging)).thenReturn(userEntityPage);
        try{
            userServiceImpl.getUsers(0,4);
        }catch (ResourceNotFoundException userExceptionMessage){
            Assertions.assertEquals(NO_USERS_FOUND,userExceptionMessage.getMessage());
        }
    }

    @Test
    void getUserByIdTest() throws  ResourceNotFoundException {
        when(userRepository.getUserById("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(user);
        User user1 = userServiceImpl.getUserById("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Assertions.assertEquals(user.getUserId(), user1.getUserId());
        Assertions.assertEquals(user.getUserName(),user1.getUserName());
        Assertions.assertEquals(user.getEmail(),user1.getEmail());
    }

    @Test
    void getUserByIdTestException(){
        when(userRepository.getUserById("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(null);
        try{
            userServiceImpl.getUserById("73578dfd-e7c9-4381-a348-113e72d80fa2");
        } catch (ResourceNotFoundException userExceptionMessage){
            Assertions.assertEquals(USER_ID_NOT_FOUND,userExceptionMessage.getMessage());
        }
    }

    @Test
    void getUserByIdCustomTest() throws UserExceptionMessage, ResourceNotFoundException {
        when(userRepository.getUserByIdCustom("a6bf0fef-01fd-465c-a901-a3b98f5c88b4")).thenReturn(userEntity);
        UserProfileResponse userProfileResponse= userServiceImpl.getUserByIdCustom("a6bf0fef-01fd-465c-a901-a3b98f5c88b4");
        Assertions.assertEquals(userEntity.getUserName(),userProfileResponse.getUser().getUserName());
    }

    @Test
    void getUserById1ExceptionTest(){
        UserDetailEntityDTO userEntity= null;
        when(userRepository.getUserByIdCustom("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userEntity);
        try{
            userServiceImpl.getUserByIdCustom("73578dfd-e7c9-4381-a348-113e72d80fa2");
        } catch (UserExceptionMessage | ResourceNotFoundException userExceptionMessage){
            Assertions.assertEquals(USER_ID_NOT_FOUND,userExceptionMessage.getMessage());
        }
    }

    @Test
    void getUserByNameTest() throws UserExceptionMessage, ResourceNotFoundException {
        userEntities.add(user);
        when(userRepository.findByNameIgnoreCase(user.getUserName())).thenReturn(userEntities);
        List<User> userEntities1 = userServiceImpl.getUserByName(user.getUserName());
        Assertions.assertEquals(userEntities.get(0).getUserName(),userEntities1.get(0).getUserName());
        Assertions.assertEquals(userEntities.get(0).getUserId(),userEntities1.get(0).getUserId());
        Assertions.assertEquals(userEntities.get(0).getEmail(),userEntities1.get(0).getEmail());
    }

    @Test
    void getUserByNameTestException() {
        when(userRepository.findByNameIgnoreCase(any())).thenReturn(userEntities);
        try {
            List<User> userEntities1 = userServiceImpl.getUserByName("vinay");
        }catch (ResourceNotFoundException userExceptionMessage){
            Assertions.assertEquals(USER_NAME_NOT_FOUND,userExceptionMessage.getMessage());
        }
    }

    @Test
    void getUserByEmailCustomUserNotPresentTest(){
        user.setUserName(null);
        when(userRepository.findByMail("vinay@gmail.com")).thenReturn(user);
        UserMailResponse userMailResponse = new UserMailResponse(DATA_NOT_FOUND,"Invitation sent to user with given email id!",null);
        UserMailResponse userMailResponseReal= userServiceImpl.getUserByEmailCustom("vinay@gmail.com",anyString());
        Assertions.assertEquals(userMailResponse.getStatus(),userMailResponseReal.getStatus());
        Assertions.assertEquals(userMailResponse.getMessage(),userMailResponseReal.getMessage());

    }

    @Test
    void getUserByEmailCustomTest(){
        when(userRepository.findByMail("vinay@gmail.com")).thenReturn(user);
        UserMailResponse userMailResponse= userServiceImpl.getUserByEmailCustom("vinay@gmail.com",anyString());
        Assertions.assertEquals(user.getUserId(),userMailResponse.getUser().getUserId());
    }


    @Test
    void getUserByEmail() throws UserExceptionMessage {
        when(userRepository.findByMail("vinay@gmail.com")).thenReturn(user);
        User user = userServiceImpl.getUserByEmail(userEntity.getEmail());
        Assertions.assertEquals(user.getUserId(),user.getUserId());
        Assertions.assertEquals(user.getEmail(),user.getEmail());
        Assertions.assertEquals(user.getUserName(),user.getUserName());
    }

    @Test
    void getUserByEmailExceptionTest() {
        user.setUserId(null);
        when(userRepository.findByMail("vinay@gmail.com")).thenReturn(user);
        userServiceImpl.getUserByEmail("vinay@gmail.com");
    }

    @Test
    void sendUserMedicinesTest() throws  FileNotFoundException, ResourceNotFoundException {
        medicineHistoryList.add(medicineHistory);
        when(userMedicineRepository.findById(12345)).thenReturn(userMedicines);
        User entity= userMedicines.get().getUser();
        List<MedicineHistory> medicinesList= userMedicines.get().getMedicineHistories();
        String text= pdfMailSender.send(entity,userMedicines.get(),medicinesList);
        UserResponse userResponseReal= userServiceImpl.sendUserMedicines(12345);
        Assertions.assertEquals(text,userResponseReal.getMessage());
    }

    @Test
    void sendUserMedicinesExceptionTest() throws FileNotFoundException {
        medicineHistoryList.add(medicineHistory);
        Optional<UserMedicines> userMedicines= Optional.empty();
        when(userMedicineRepository.findById(12345)).thenReturn(userMedicines);
        try {
            userServiceImpl.sendUserMedicines(12345);
        }catch (ResourceNotFoundException e) {
            Assertions.assertEquals(MEDICINES_NOT_FOUND,e.getMessage());
        }
    }


    @Test
    void loginTest() throws UserExceptionMessage, ResourceNotFoundException {
        when(userServiceImpl.getUserByEmail(any())).thenReturn(user);
        userDetails.setFcmToken("afyuauvfiualfviuaofga");
        when(userDetailsRepository.save(userDetails)).thenReturn(userDetails);
        String jwtToken= jwtUtil.generateToken(user.getUserName());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());
        UserResponse userResponse= new UserResponse("Success", "Logged In",new ArrayList<>(Arrays.asList(user)),jwtToken,refreshToken);
        UserResponse userResponse1= userServiceImpl.login("nikunj123@gmail.com","afyuauvfiualfviuaofga");
        Assertions.assertEquals(userResponse.getUser().get(0).getUserId(),userResponse1.getUser().get(0).getUserId());
        Assertions.assertEquals(userResponse.getUser().get(0).getUserName(),userResponse1.getUser().get(0).getUserName());
        Assertions.assertEquals(userResponse.getUser().get(0).getEmail(),userResponse1.getUser().get(0).getEmail());

    }

    @Test
    void loginExceptionTest() {
        user.setUserName(null);
        when(userServiceImpl.getUserByEmail(any())).thenReturn(user);
        userDetails.setFcmToken("afyuauvfiualfviuaofga");
        when(userDetailsRepository.save(userDetails)).thenReturn(userDetails);
        try{
            userServiceImpl.login(user.getEmail(), user.getUserDetails().getFcmToken());
        }catch (UserExceptionMessage | ResourceNotFoundException userExceptionMessage){
            Assertions.assertEquals(USER_NOT_FOUND,userExceptionMessage.getMessage());
        }
    }

    @Test
    void getUserMedicinesByIdTest() throws UserExceptionMessage {
        UserMedicineDTO userMedicineDTO= new UserMedicineDTO("PCM","Somthing","take 2 times a day",null,null,"12:00 AM","Mon");
        List<UserMedicineDTO> medicineDTOList = new ArrayList<>();
        medicineDTOList.add(userMedicineDTO);
        when(userRepository.getUserMedicineById("a6bf0fef-01fd-465c-a901-a3b98f5c88b4")).thenReturn(medicineDTOList);
        List<UserMedicineDTO> userMedicineDTOS = userServiceImpl.getUserMedicineById("a6bf0fef-01fd-465c-a901-a3b98f5c88b4");
        Assertions.assertEquals(medicineDTOList.get(0).getMedicineName(),userMedicineDTOS.get(0).getMedicineName());
        Assertions.assertEquals(medicineDTOList.get(0).getMedicineDescription(),userMedicineDTOS.get(0).getMedicineDescription());
        Assertions.assertEquals(medicineDTOList.get(0).getDays(),userMedicineDTOS.get(0).getDays());
        Assertions.assertEquals(medicineDTOList.get(0).getEndDate(),userMedicineDTOS.get(0).getEndDate());
        Assertions.assertEquals(medicineDTOList.get(0).getStartDate(),userMedicineDTOS.get(0).getStartDate());

    }

    @Test
    void getUserMedicinesByIdExceptionTest(){
        when(userRepository.getUserMedicineById("a6bf0fef-01fd-465c-a901-a3b98f5c88b4")).thenReturn(null);
        try {
            userServiceImpl.getUserMedicineById("a6bf0fef-01fd-465c-a901-a3b98f5c88b4");
        }catch (UserExceptionMessage userExceptionMessage)
        {Assertions.assertEquals(NO_MEDICINE_PRESENT,userExceptionMessage.getMessage());
        }
    }

    @Test
    void getRefreshTokenTest() throws UserExceptionMessage, ResourceNotFoundException {
        HttpServletRequest httpServletRequest= Mockito.mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer header");
        when(userRepository.getUserById("a6bf0fef-01fd-465c-a901-a3b98f5c88b4")).thenReturn(user);
        when(jwtUtil.validateToken("header",null)).thenReturn(true);
        when(jwtUtil.extractUsername("header")).thenReturn("vinay");
        when(jwtUtil.generateToken("vinay")).thenReturn("some value");
        RefreshTokenResponse response= new RefreshTokenResponse(SUCCESS,"some value","something");
        RefreshTokenResponse responseReal = userServiceImpl.getRefreshToken(httpServletRequest,"a6bf0fef-01fd-465c-a901-a3b98f5c88b4");
        Assertions.assertEquals(response.getStatus(),responseReal.getStatus());
    }

    @Test
    void getRefreshTokenTestExpiredRefreshToken() throws UserExceptionMessage, ResourceNotFoundException {
        HttpServletRequest httpServletRequest= Mockito.mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer header");
        when(userRepository.getUserById("a6bf0fef-01fd-465c-a901-a3b98f5c88b4")).thenReturn(user);
        when(jwtUtil.validateToken("header",null)).thenReturn(false);
        when(jwtUtil.generateToken("vinay")).thenReturn("some value");
        when(jwtUtil.generateRefreshToken("vinay")).thenReturn("some value");
        RefreshTokenResponse response= new RefreshTokenResponse(SUCCESS,"some value","some value");
        RefreshTokenResponse responseReal = userServiceImpl.getRefreshToken(httpServletRequest,"a6bf0fef-01fd-465c-a901-a3b98f5c88b4");
        Assertions.assertEquals(response.getStatus(),responseReal.getStatus());
    }

    @Test
    void getRefreshTokenTestException() throws UserExceptionMessage, ResourceNotFoundException {
        HttpServletRequest httpServletRequest= Mockito.mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(null);
        when(userRepository.getUserById("a6bf0fef-01fd-465c-a901-a3b98f5c88b4")).thenReturn(user);
        try {
            userServiceImpl.getRefreshToken(httpServletRequest, "a6bf0fef-01fd-465c-a901-a3b98f5c88b4");
        }catch (UserExceptionMessage message){
            Assertions.assertEquals(INVALID_REFRESH_TOKEN, message.getMessage());
        }
    }

    @Test
    void getRefreshTokenTestExceptionOne() throws ResourceNotFoundException {
        HttpServletRequest httpServletRequest= Mockito.mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Beare header");
        when(userRepository.getUserById("a6bf0fef-01fd-465c-a901-a3b98f5c88b4")).thenReturn(user);
        try {
            userServiceImpl.getRefreshToken(httpServletRequest, "a6bf0fef-01fd-465c-a901-a3b98f5c88b4");
        }catch (UserExceptionMessage message){
            Assertions.assertEquals(INVALID_REFRESH_TOKEN, message.getMessage());
        }
    }

    @Test
    void checkRefreshTokenTest(){
        when(jwtUtil.validateToken("something",null)).thenThrow(ExpiredJwtException.class);
        Boolean value = userServiceImpl.checkRefreshToken("something");
        Assertions.assertEquals(false,value);

    }

}
