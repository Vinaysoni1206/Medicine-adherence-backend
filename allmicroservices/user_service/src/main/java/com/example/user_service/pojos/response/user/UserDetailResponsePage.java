package com.example.user_service.pojos.response.user;

import com.example.user_service.pojos.dto.user.UserDetailEntityDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailResponsePage {
    private String status;
    private  String message;
    private long totalItems;
    private int totalPages;
    private int currentPage;
    private List<UserDetailEntityDTO> users;



}
