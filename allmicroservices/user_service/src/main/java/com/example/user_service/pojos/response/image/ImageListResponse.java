package com.example.user_service.pojos.response.image;


import com.example.user_service.model.image.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageListResponse {
    private String status;
    private String message;
    private List<Image> imageList;
}
