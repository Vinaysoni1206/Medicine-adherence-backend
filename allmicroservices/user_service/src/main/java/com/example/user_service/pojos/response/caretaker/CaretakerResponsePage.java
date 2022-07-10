package com.example.user_service.pojos.response.caretaker;

import com.example.user_service.model.user.UserCaretaker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
