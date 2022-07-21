package com.example.user_service.pojos.request;


import com.example.user_service.annotations.Email;
import com.example.user_service.annotations.EmptyNotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.user_service.util.Constants.NullEmptyConstants.USER_NAME_EMPTY_NULL;


/**
 * This is a pojo class for user with mail details
 */
@Data
@NoArgsConstructor
public class UserMailDTO {
    @EmptyNotNull(message = USER_NAME_EMPTY_NULL)
    private String userName;
    @Email
    private String email;
    private String picPath;

    public UserMailDTO(String userName, String email, String picPath){
        this.userName= userName;
        this.email = email;
        this.picPath = picPath;
    }

}
