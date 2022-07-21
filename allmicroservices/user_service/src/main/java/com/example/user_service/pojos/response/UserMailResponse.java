package com.example.user_service.pojos.response;


import com.example.user_service.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a response class for User with mail info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMailResponse {
    private  String status;
    private String message;
    private User user;
}
