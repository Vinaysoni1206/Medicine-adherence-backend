package com.example.user_service.pojos.request;

import com.example.user_service.annotations.EmptyNotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.user_service.util.Constants.NullEmptyConstants.*;

/**
 * This is a pojo class for user details
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {
    @EmptyNotNull(message = BIO_EMPTY_NULL)
    private String bio;
    private int age;
    private Long userContact;
    @EmptyNotNull(message = GENDER_EMPTY_NULL)
    private String gender;
    @EmptyNotNull(message = BLOOD_GROUP_EMPTY_NULL)
    private String bloodGroup;
    @EmptyNotNull(message = MARITAL_STATUS_EMPTY_NULL)
    private String maritalStatus;
    private int weight;
}