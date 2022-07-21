package com.example.user_service.pojos.request;

import com.example.user_service.annotations.Email;
import com.example.user_service.annotations.EmptyNotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.user_service.util.Constants.NullEmptyConstants.FCM_TOKEN_NULL_EMPTY;

/**
 * This is a pojo class for Login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @EmptyNotNull(message = FCM_TOKEN_NULL_EMPTY)
    private String fcmToken;
    @Email
    private String email;

}
