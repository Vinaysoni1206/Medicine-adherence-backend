package com.example.user_service.pojos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a response class for SQL error
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqlErrorResponse {

    private String message;
    private String status;


}
