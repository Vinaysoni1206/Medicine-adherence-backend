package com.example.user_service.pojos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  This is a response class for an Image
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse {
    private String status;
    private String message;
}
