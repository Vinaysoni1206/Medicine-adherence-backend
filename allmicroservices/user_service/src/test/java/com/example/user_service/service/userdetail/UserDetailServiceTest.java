package com.example.user_service.service.userdetail;

import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.UserDetails;
import com.example.user_service.model.User;
import com.example.user_service.pojos.request.UserDetailsDTO;
import com.example.user_service.pojos.response.UserDetailResponse;
import com.example.user_service.repository.UserDetailsRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.impl.UserDetailServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static com.example.user_service.util.Constants.SUCCESS;
import static com.example.user_service.util.Constants.USER_NOT_FOUND;
import static org.mockito.Mockito.when;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserDetailServiceTest {

    private UserDetailServiceImpl userDetailServiceImpl;
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetailsRepository userDetailsRepository;



    @BeforeEach
    public void initCase(){
        userDetailServiceImpl = new UserDetailServiceImpl(userDetailsRepository,userRepository);
    }


    @Test
    void saveUserDetailTest()   {
        UserDetailsDTO userDetailsDTO= new UserDetailsDTO("Something",21,null,"male","AB+","Unmarried",60);
        when(userRepository.getUserById("feyiafiafgiagfieagfi")).thenReturn(null);
       try {
           userDetailServiceImpl.saveUserDetail("feyiafiafgiagfieagfi", userDetailsDTO);
       }catch(ResourceNotFoundException userExceptionMessage) {
           Assertions.assertEquals(USER_NOT_FOUND,userExceptionMessage.getMessage());
       }
    }

       @Test
    void saveUserDetailExceptionTest() throws  ResourceNotFoundException {
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO("Something", 21, 352789L, "male", "AB+", "Unmarried", 60);
           UserDetails userDetails = new UserDetails("73578dfd-e7c9-4381-a348-113e72d80fa2","something",null,21,null,414124,null,5L,56L,null,"male","AB+","Unmarried",60,2314,null,null);
           User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),userDetails,null);
        when(userRepository.getUserById("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(user);
        UserDetailResponse userDetailResponse= new UserDetailResponse(SUCCESS,"Saved user details",userDetails);
        UserDetailResponse userDetailsResponseReal= userDetailServiceImpl.saveUserDetail("73578dfd-e7c9-4381-a348-113e72d80fa2",userDetailsDTO);
        Assertions.assertEquals(userDetailResponse.getUserDetails().getGender(),userDetailsResponseReal.getUserDetails().getGender());
        Assertions.assertEquals(userDetailResponse.getUserDetails().getBio(),userDetailsResponseReal.getUserDetails().getBio());
        Assertions.assertEquals(userDetailResponse.getUserDetails().getAge(),userDetailsResponseReal.getUserDetails().getAge());
        Assertions.assertEquals(userDetailResponse.getUserDetails().getBloodGroup(),userDetailsResponseReal.getUserDetails().getBloodGroup());

       }

}