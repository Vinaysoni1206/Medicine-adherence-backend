package com.example.user_service.pojos.dto.user;

import lombok.Data;

@Data
public class UserDetailEntityDTO {

    private String userName;
    private String email;
    private String bio;
    private int age;
    private Long userContact;
    private String gender;
    private String bloodGroup;
    private String maritalStatus;
    private int weight;

    public UserDetailEntityDTO(String userName, String email, String bio, int age, Long userContact, String gender,
                               String bloodGroup, String maritalStatus, int weight) {
        this.userName = userName;
        this.email = email;
        this.bio = bio;
        this.age = age;
        this.userContact = userContact;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.maritalStatus = maritalStatus;
        this.weight = weight;

    }

}
