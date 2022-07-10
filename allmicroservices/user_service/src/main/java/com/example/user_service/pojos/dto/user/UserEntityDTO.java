package com.example.user_service.pojos.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserEntityDTO {

    private String userName;
    private String email;

    public UserEntityDTO(String userName,String email){
        this.userName= userName;
        this.email= email;
    }
}
