package com.example.user_service.pojos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendImageDto {
    @NotNull(message = "Image file cannot be null")
//    @ImageValidation
    private MultipartFile image;

    @NotNull(message = "Name cannot be empty")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Medicine name cannot be null")
    @NotBlank(message = "Medicine name cannot be blank")
    private String medName;

    @NotNull(message = "Id cannot be null")
    @NotBlank(message = "Id cannot be blank")
    private String id;

    @NotNull(message = "Medicine Id cannot be null")
    private Integer medId;
}
