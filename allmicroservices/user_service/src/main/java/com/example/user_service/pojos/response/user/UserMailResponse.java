package com.example.user_service.pojos.response.user;


import com.example.user_service.pojos.dto.user.UserMailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMailResponse {
    private  String status;
    private String message;
    private UserMailDTO userMailDTO;
}
