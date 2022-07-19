package com.example.user_service.pojos.response;

import com.example.user_service.pojos.request.UserDetailEntityDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This is a response class for list of user along with details
 */
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
