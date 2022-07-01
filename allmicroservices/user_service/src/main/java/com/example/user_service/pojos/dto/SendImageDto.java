package com.example.user_service.pojos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendImageDto {
    private MultipartFile image;
    private String name;
    private String medName;
    private String id;
    private Integer medId;
}
