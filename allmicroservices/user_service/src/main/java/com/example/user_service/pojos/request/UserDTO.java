package com.example.user_service.pojos.request;

import com.example.user_service.annotations.Email;
import com.example.user_service.annotations.EmptyNotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import static com.example.user_service.util.Constants.NullEmptyConstants.USER_NAME_EMPTY_NULL;

/**
 * This is a pojo class for User
 */
@Data
@NoArgsConstructor
public class UserDTO {

    @EmptyNotNull(message = USER_NAME_EMPTY_NULL)
    private String userName;
    @Email
    private String email;

    public UserDTO(String userName, String email){
        this.userName= userName;
        this.email= email;
    }
}
