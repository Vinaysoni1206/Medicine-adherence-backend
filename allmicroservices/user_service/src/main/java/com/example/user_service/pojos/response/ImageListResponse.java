package com.example.user_service.pojos.response;


import com.example.user_service.model.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This is a response class for list of images
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageListResponse {
    private String status;
    private String message;
    private List<Image> imageList;
}
