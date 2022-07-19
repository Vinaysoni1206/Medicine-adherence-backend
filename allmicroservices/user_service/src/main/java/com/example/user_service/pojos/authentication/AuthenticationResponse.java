package com.example.user_service.pojos.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a Pojo response class for JWT Authentication
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private String jwt;

}
