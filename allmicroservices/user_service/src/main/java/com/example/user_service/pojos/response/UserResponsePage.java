package com.example.user_service.pojos.response;

import com.example.user_service.pojos.request.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a response class for a user
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponsePage {


        private String status;
        private String message;
        private UserDTO user;

    }

