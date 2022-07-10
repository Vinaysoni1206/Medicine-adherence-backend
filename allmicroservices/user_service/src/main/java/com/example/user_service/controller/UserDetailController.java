package com.example.user_service.controller;



import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.user.UserDetails;
import com.example.user_service.pojos.dto.user.UserDetailsDTO;
import com.example.user_service.pojos.response.user.UserDetailResponse;
import com.example.user_service.service.userdetail.UserDetailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/v1")
@Validated
public class UserDetailController {
    private final UserDetailService userDetailService;

    UserDetailController(UserDetailService userDetailService){
        this.userDetailService=userDetailService;
    }

    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @PutMapping(value = "/user-details" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDetailResponse> updateUserDetails(@NotNull @NotBlank @RequestParam("userId") String id,
                                                                @Valid @RequestBody UserDetailsDTO userDetailsDTO) throws UserExceptionMessage {
        UserDetails userDetails = userDetailService.saveUserDetail(id,userDetailsDTO);
        UserDetailResponse userDetailResponse= new UserDetailResponse("Success","Saved user details",userDetails);
        return new ResponseEntity<>(userDetailResponse,HttpStatus.OK);

    }

}
