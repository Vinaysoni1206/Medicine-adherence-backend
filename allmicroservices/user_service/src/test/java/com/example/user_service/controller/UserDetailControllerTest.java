package com.example.user_service.controller;

import com.example.user_service.model.user.UserDetails;
import com.example.user_service.pojos.dto.user.UserDetailsDTO;
import com.example.user_service.service.userdetail.UserDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.http.ResponseEntity.status;


@RunWith(SpringJUnit4ClassRunner.class)
class UserDetailControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper= new ObjectMapper();
    @InjectMocks
    private UserDetailController userDetailController;

    @Mock
    UserDetailService userDetailService;

    @BeforeEach
    private void setUp(){
        this.mockMvc = MockMvcBuilders.standaloneSetup(userDetailController).build();
    }

    UserDetails userDetails= new UserDetails();

    @Test
    @ExtendWith(MockitoExtension.class)
    void updateuserDetails() throws Exception{
        UserDetailsDTO userDetailsDTO= new UserDetailsDTO();
        String jsonTest = objectMapper.writeValueAsString(userDetailsDTO);
        Mockito.when(userDetailService.saveUserDetail("534851457147",userDetailsDTO)).thenReturn(userDetails);
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/user-details?userId=534851457147")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonTest))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


}