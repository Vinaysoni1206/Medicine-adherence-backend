package com.example.user_service.controller;



import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.UserDetails;
import com.example.user_service.pojos.request.UserDetailsDTO;
import com.example.user_service.pojos.response.UserDetailResponse;
import com.example.user_service.service.UserDetailService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.example.user_service.util.Constants.*;

/**
 * The controller class is responsible for processing incoming REST API requests
 */
@RestController
@RequestMapping("/api/v1")
@Validated
public class UserDetailController {
    private final UserDetailService userDetailService;

    UserDetailController(UserDetailService userDetailService){
        this.userDetailService=userDetailService;
    }


    /**
     * Updates user details with respect to its id
     */
    @ApiOperation(value = "Updates user details with respect to its id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated user details successfully"),
            @ApiResponse(code = 404,message = USER_NOT_FOUND),
            @ApiResponse(code = 401, message = UNAUTHORIZED)})
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @PutMapping(value = "/user-details" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDetailResponse> updateUserDetails(@NotNull @NotBlank @RequestParam("userId") String userId,
                                                                @Valid @RequestBody UserDetailsDTO userDetailsDTO) throws UserExceptionMessage, ResourceNotFoundException {
        return new ResponseEntity<>(userDetailService.saveUserDetail(userId,userDetailsDTO),HttpStatus.OK);

    }

}
