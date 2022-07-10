package com.example.user_service.pojos.response.user;

import com.example.user_service.pojos.dto.user.UserDetailEntityDTO;
import com.example.user_service.pojos.dto.user.UserMedicineDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    private String status;
    private String message;
    private UserDetailEntityDTO user;
    private List<UserMedicineDTO> userMedicines;

}