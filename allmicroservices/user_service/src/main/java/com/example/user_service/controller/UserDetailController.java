package com.example.user_service.controller;



import com.example.user_service.exception.UserexceptionMessage;
import com.example.user_service.model.UserDetails;
import com.example.user_service.pojos.dto.UserDetailsDTO;
import com.example.user_service.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserDetailController {


    @Autowired
    private UserDetailService userDetailService;

    @PutMapping(value = "/userdetails" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDetails> updateUserDetails(@RequestParam("id") String id,
                                                         @RequestBody UserDetailsDTO userDetailsDTO) throws UserexceptionMessage {

        return new ResponseEntity<>(userDetailService.saveUserDetail(id,userDetailsDTO),HttpStatus.OK);

    }

}
//