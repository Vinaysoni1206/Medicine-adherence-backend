package com.example.user_service.pojos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a pojo class for Login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    private String fcmToken;
    private String email;

}
