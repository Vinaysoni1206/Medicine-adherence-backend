package com.example.user_service.pojos.response;

import com.example.user_service.pojos.request.UserDetailEntityDTO;
import com.example.user_service.pojos.request.UserMedicineDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This is a response class for User along with details and medicine info
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    private String status;
    private String message;
    private UserDetailEntityDTO user;
    private List<UserMedicineDTO> userMedicines;

}