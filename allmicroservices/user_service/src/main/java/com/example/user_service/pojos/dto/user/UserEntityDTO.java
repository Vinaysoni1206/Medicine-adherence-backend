package com.example.user_service.pojos.dto.user;

import lombok.Data;

@Data
public class UserEntityDTO {

    private String userName;
    private String email;

    public UserEntityDTO(String userName,String email){
        this.userName= userName;
        this.email= email;
    }
}
