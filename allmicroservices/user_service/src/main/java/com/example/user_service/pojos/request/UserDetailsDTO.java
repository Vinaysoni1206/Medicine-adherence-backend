package com.example.user_service.pojos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a pojo class for user details
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {
    private String bio;
    private int age;
    private Long userContact;
    private String gender;
    private String bloodGroup;
    private String maritalStatus;
    private int weight;
}