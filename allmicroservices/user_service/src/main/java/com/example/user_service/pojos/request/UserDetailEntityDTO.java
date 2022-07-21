package com.example.user_service.pojos.request;

import com.example.user_service.annotations.Email;
import com.example.user_service.annotations.EmptyNotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.user_service.util.Constants.NullEmptyConstants.*;

/**
 * This is a Pojo class for User along with details
 */
@Data
@NoArgsConstructor
public class UserDetailEntityDTO {

    @EmptyNotNull(message = USER_NAME_EMPTY_NULL)
    private String userName;
    @Email
    private String email;
    @EmptyNotNull(message = BIO_EMPTY_NULL)
    private String bio;
    @EmptyNotNull(message = AGE_EMPTY_NULL)
    private int age;
    @EmptyNotNull(message = CONTACT_EMPTY_NULL)
    private Long userContact;
    @EmptyNotNull(message = GENDER_EMPTY_NULL)
    private String gender;
    @EmptyNotNull(message = BLOOD_GROUP_EMPTY_NULL)
    private String bloodGroup;
    @EmptyNotNull(message = MARITAL_STATUS_EMPTY_NULL)
    private String maritalStatus;
    @EmptyNotNull(message = WEIGHT_EMPTY_NULL)
    private int weight;

    public UserDetailEntityDTO(String userName, String email, String bio, int age, Long userContact, String gender,String bloodGroup, String maritalStatus, int weight) {                                   //NOSONAR
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
