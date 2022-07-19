package com.example.user_service.pojos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a response class while caretaker is deleted
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaretakerDelete {

    private String status;
    private String message;
}
