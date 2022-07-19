package com.example.user_service.pojos.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a pojo class for User
 */
@Data
@NoArgsConstructor
public class UserDTO {

    private String userName;
    private String email;

    public UserDTO(String userName, String email){
        this.userName= userName;
        this.email= email;
    }
}
