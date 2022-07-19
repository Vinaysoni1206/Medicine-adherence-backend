package com.example.user_service.service.user;

import com.example.user_service.config.PdfMailSender;
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
import com.example.user_service.pojos.response.UserDetailResponsePage;
import com.example.user_service.pojos.response.UserResponse;
import com.example.user_service.repository.UserDetailsRepository;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserServiceImpl;
import com.example.user_service.util.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
    @BeforeEach
    public void initcase() {
        userServiceImpl= new UserServiceImpl(userRepository,jwtUtil,userDetailsRepository,mapper,pdfMailSender,userMedicineRepository);
    }

    @Test
    @DisplayName("Test for saving user successfully")
    void saveUserTest() throws UserExceptionMessage{

        User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
        UserResponse userResponse= new UserResponse("Failed","User is already present",new ArrayList<>(Arrays.asList(user)),null,null);
        when(userServiceImpl.getUserByEmail(user.getEmail())).thenReturn(null);
        UserDTO userDTO = new UserDTO("vinay","vinay@gmail.com");
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
    void saveUserTestUserExist() throws UserExceptionMessage{

        User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
        UserResponse userResponse= new UserResponse("Failed","User is already present",new ArrayList<>(Arrays.asList(user)),null,null);
        UserDTO userDTO = new UserDTO("vinay","vinay@gmail.com");
        when(userServiceImpl.getUserByEmail(user.getEmail())).thenReturn(user);
        UserResponse userResponse1= userServiceImpl.saveUser(userDTO,"eafyigfiagf","sfhoshgouahgo");
        Assertions.assertEquals(userResponse.getUser().get(0).getUserName(),userResponse1.getUser().get(0).getUserName());
        Assertions.assertEquals(userResponse.getUser().get(0).getUserId(),userResponse1.getUser().get(0).getUserId());
        Assertions.assertEquals(userResponse.getUser().get(0).getEmail(),userResponse1.getUser().get(0).getEmail());

    }

    @Test
    @DisplayName("Test for saving user exception")
    void saveUserInnerExcpetion(){
        User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2",null,"vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
        UserDTO userDTO = new UserDTO("vinay","vinay@gmail.com");
        when(userServiceImpl.getUserByEmail(any())).thenReturn(null);
        when(mapper.map(userDTO, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        try{
            userServiceImpl.saveUser(userDTO,"eafyigfiagf","sfhoshgouahgo");
        }catch(UserExceptionMessage userExceptionMessage){
            Assertions.assertEquals("Error try again!",userExceptionMessage.getMessage());
        }
    }


    @Test
    @DisplayName("Test for getting users list successfully")
    void getUsersTest() throws Exception{
        UserDetailEntityDTO userEntity = new UserDetailEntityDTO("vinay","vinay@gmail.com","something",21,null,"Male","AB+","UnMarried",60);
        UserDetailEntityDTO userEntity1 = new UserDetailEntityDTO("yatin","vinay@gmail.com","something",21,null,"Male","AB+","UnMarried",60);
        UserDetailEntityDTO userEntity2 = new UserDetailEntityDTO("nikunj","vinay@gmail.com","something",21,null,"Male","AB+","UnMarried",60);
        List<UserDetailEntityDTO> users = new ArrayList<>();
        users.add(userEntity);
        users.add(userEntity1);
        users.add(userEntity2);
        int pageSize = 4;
        int pageNo = 0;
        Pageable paging = PageRequest.of(pageNo,pageSize);
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
        List<UserDetailEntityDTO> users = new ArrayList<>();
        int pageSize = 4;
        int pageNo = 0;
        Pageable paging = PageRequest.of(pageNo,pageSize);
        Page<UserDetailEntityDTO> userEntityPage= new PageImpl<>(users);
        when(userRepository.findAllUsers(paging)).thenReturn(userEntityPage);
        try{
            CompletableFuture<UserDetailResponsePage> userResponsePage1=userServiceImpl.getUsers(0,4);
        }catch (UserExceptionMessage userExceptionMessage){
            Assertions.assertEquals("Data not found",userExceptionMessage.getMessage());
        }
    }

    @Test
    void getUserByIdTest() throws UserExceptionMessage{
        User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
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
            User user1 = userServiceImpl.getUserById("73578dfd-e7c9-4381-a348-113e72d80fa2");
        } catch (UserExceptionMessage userExceptionMessage){
            Assertions.assertEquals("Data not found",userExceptionMessage.getMessage());
        }
    }

    @Test
    void getUserById1Test() throws UserExceptionMessage {
        UserDetailEntityDTO userDetailEntityDTO= new UserDetailEntityDTO("Vinay","vinay@gmail.com","nothing",21,null,"male","AB+","Unmarried",60);
        when(userRepository.getUserById1("a6bf0fef-01fd-465c-a901-a3b98f5c88b4")).thenReturn(userDetailEntityDTO);
        UserDetailEntityDTO userDetailEntityDTO1= userServiceImpl.getUserById1("a6bf0fef-01fd-465c-a901-a3b98f5c88b4");
        Assertions.assertEquals(userDetailEntityDTO.getUserName(),userDetailEntityDTO1.getUserName());
        Assertions.assertEquals(userDetailEntityDTO.getAge(),userDetailEntityDTO1.getAge());
        Assertions.assertEquals(userDetailEntityDTO.getGender(),userDetailEntityDTO1.getGender());
        Assertions.assertEquals(userDetailEntityDTO.getBio(),userDetailEntityDTO1.getBio());
        Assertions.assertEquals(userDetailEntityDTO.getEmail(),userDetailEntityDTO1.getEmail());
    }

    @Test
    void getUserById1ExceptionTest(){
        UserDetailEntityDTO userEntity= null;
        when(userRepository.getUserById1("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userEntity);
        try{
            UserDetailEntityDTO userEntity1 = userServiceImpl.getUserById1("73578dfd-e7c9-4381-a348-113e72d80fa2");
        } catch (UserExceptionMessage userExceptionMessage){
            Assertions.assertEquals("Data not found",userExceptionMessage.getMessage());
        }
    }

    @Test
    void getUserByNameTest() throws UserExceptionMessage {
        User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
        List<User> userEntities = new ArrayList<>();
        userEntities.add(user);
        when(userRepository.findByNameIgnoreCase(user.getUserName())).thenReturn(userEntities);
        List<User> userEntities1 = userServiceImpl.getUserByName(user.getUserName());
        Assertions.assertEquals(userEntities.get(0).getUserName(),userEntities1.get(0).getUserName());
        Assertions.assertEquals(userEntities.get(0).getUserId(),userEntities1.get(0).getUserId());
        Assertions.assertEquals(userEntities.get(0).getEmail(),userEntities1.get(0).getEmail());
    }

    @Test
    void getUserByNameTestException() {
        List<User> userEntities = new ArrayList<>();
        when(userRepository.findByNameIgnoreCase(any())).thenReturn(userEntities);
        try {
            List<User> userEntities1 = userServiceImpl.getUserByName("vinay");
        }catch (UserExceptionMessage userExceptionMessage){
            Assertions.assertEquals("Data not found",userExceptionMessage.getMessage());
        }
    }

    @Test
    void getUserByEmail1Test() throws UserExceptionMessage {
        UserMailDTO userMailDTO= new UserMailDTO("Vinay","vinay@gmail.com",null);
        when(userRepository.findByMail1("vinay@gmail.com")).thenReturn(userMailDTO);
        UserMailDTO userMailDTO1= userServiceImpl.getUserByEmail1("vinay@gmail.com");
        Assertions.assertEquals(userMailDTO.getUserName(),userMailDTO1.getUserName());
        Assertions.assertEquals(userMailDTO.getEmail(),userMailDTO1.getEmail());
        Assertions.assertEquals(userMailDTO.getPicPath(),userMailDTO1.getPicPath());
    }

    @Test
    void getUserByEmail1ExceptionTest() {
        UserMailDTO userMailDTO= new UserMailDTO(null,"vinay@gmail.com",null);
        when(userRepository.findByMail1("vinay@gmail.com")).thenReturn(userMailDTO);
        try{
            UserMailDTO userMailDTO1= userServiceImpl.getUserByEmail1("vinay@gmail.com");
        }catch (UserExceptionMessage userExceptionMessage){
            Assertions.assertEquals("Data not found",userExceptionMessage.getMessage());
        }

    }

    @Test
    void getUserByEmail() throws UserExceptionMessage {
        User userEntity = new User("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
        when(userRepository.findByMail("vinay@gmail.com")).thenReturn(userEntity);
        User user = userServiceImpl.getUserByEmail(userEntity.getEmail());
        Assertions.assertEquals(userEntity.getUserId(),user.getUserId());
        Assertions.assertEquals(userEntity.getEmail(),user.getEmail());
        Assertions.assertEquals(userEntity.getUserName(),user.getUserName());
    }

    @Test
    void sendUserMedicinesTest() throws UserExceptionMessage, FileNotFoundException {
        User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
        MedicineHistory medicineHistory = new MedicineHistory(123,"2022-04-16T16:05:20.961Z","10:00 AM","8:00 PM",null);
        List<MedicineHistory> medicineHistoryList= new ArrayList<>();
        medicineHistoryList.add(medicineHistory);
        Optional<UserMedicines> userMedicines= Optional.of(new UserMedicines(12345, "2022-04-16T16:05:20.961Z", "PCM", "for headache", "Mon", "2022-04-16T16:05:20.961Z", "10:00 AM", "10 AM", 12, 12, user, medicineHistoryList, null));
        when(userMedicineRepository.findById(12345)).thenReturn(userMedicines);
        User entity= userMedicines.get().getUser();
        List<MedicineHistory> medicinesList= userMedicines.get().getMedicineHistories();
        String text= pdfMailSender.send(entity,userMedicines.get(),medicinesList);
        String text1= userServiceImpl.sendUserMedicines(12345);
        Assertions.assertEquals(text,text1);
    }

    @Test
    void sendUserMedicinesExceptionTest() throws FileNotFoundException {
        User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
        MedicineHistory medicineHistory = new MedicineHistory(123,"2022-04-16T16:05:20.961Z","10:00 AM","8:00 PM",null);
        List<MedicineHistory> medicineHistoryList= new ArrayList<>();
        medicineHistoryList.add(medicineHistory);
        Optional<UserMedicines> userMedicines= Optional.empty();
        when(userMedicineRepository.findById(12345)).thenReturn(userMedicines);
        try {
            userServiceImpl.sendUserMedicines(12345);
        }catch (UserExceptionMessage e) {
            Assertions.assertEquals("Medicine not found",e.getMessage());
        }
    }


    @Test
    void loginTest() throws UserExceptionMessage {
        String fcmToken = "";
        UserDetails userDetails= new UserDetails();
        User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2","Nikunj123","nikunj123@gmail.com", LocalDateTime.now(), LocalDateTime.now(),userDetails,null);
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
    void loginExceptionTest(){
        String fcmToken = "";
        UserDetails userDetails= new UserDetails();
        User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2",null,"nikunj123@gmail.com", LocalDateTime.now(), LocalDateTime.now(),userDetails,null);
        when(userServiceImpl.getUserByEmail(any())).thenReturn(user);
        userDetails.setFcmToken("afyuauvfiualfviuaofga");
        when(userDetailsRepository.save(userDetails)).thenReturn(userDetails);
        String jwtToken= jwtUtil.generateToken(user.getUserName());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());
        try{
            userServiceImpl.login(user.getEmail(), user.getUserDetails().getFcmToken());
        }catch (UserExceptionMessage userExceptionMessage){
            Assertions.assertEquals("Data not found",userExceptionMessage.getMessage());
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
        Assertions.assertEquals(medicineDTOList.get(0).getMedicineDes(),userMedicineDTOS.get(0).getMedicineDes());
        Assertions.assertEquals(medicineDTOList.get(0).getDays(),userMedicineDTOS.get(0).getDays());
        Assertions.assertEquals(medicineDTOList.get(0).getEndDate(),userMedicineDTOS.get(0).getEndDate());
        Assertions.assertEquals(medicineDTOList.get(0).getStartDate(),userMedicineDTOS.get(0).getStartDate());

    }

    @Test
    void getUserMedicinesByIdExceptionTest() throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException {
        UserMedicineDTO userMedicineDTO= null;
        List<UserMedicineDTO> medicineDTOList = new ArrayList<>();
        when(userRepository.getUserMedicineById("a6bf0fef-01fd-465c-a901-a3b98f5c88b4")).thenReturn(null);
        try {
            userServiceImpl.getUserMedicineById("a6bf0fef-01fd-465c-a901-a3b98f5c88b4");
        }catch (UserExceptionMessage userExceptionMessage)
        {Assertions.assertEquals("Data not found",userExceptionMessage.getMessage());
        }
    }






}
