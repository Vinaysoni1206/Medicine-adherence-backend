package com.example.user_service.pojos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @NotNull(message = "Token cannot be empty")
    @NotBlank(message = "Token cannot be empty")
    private String fcmToken;

    @NotNull(message = "Email cannot be empty")
    @NotBlank(message = "Email cannot be empty")
    private String email;

}
