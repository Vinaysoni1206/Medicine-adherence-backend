package com.example.user_service.pojos.response;

import com.example.user_service.model.UserCaretaker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This is  a response class for List of caretaker with pagination
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaretakerResponsePage {
    private String status;
    private  String message;
    private long totalItems;
    private int totalPages;
    private int currentPage;
    private List<UserCaretaker> userCaretaker;
}
