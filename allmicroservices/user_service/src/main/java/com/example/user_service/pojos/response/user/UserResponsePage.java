package com.example.user_service.pojos.response.user;

import com.example.user_service.pojos.dto.user.UserEntityDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponsePage {


        private String status;
        private String message;
        private UserEntityDTO user;

    }

