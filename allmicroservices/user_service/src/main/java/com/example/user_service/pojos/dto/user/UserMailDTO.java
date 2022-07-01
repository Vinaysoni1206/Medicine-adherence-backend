package com.example.user_service.pojos.dto.user;


import lombok.Data;

@Data
public class UserMailDTO {
    private String userName;
    private String email;
    private String picPath;

    public UserMailDTO(String userName, String email, String picPath){
        this.userName= userName;
        this.email = email;
        this.picPath = picPath;
    }
}
