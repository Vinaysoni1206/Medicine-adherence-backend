package com.example.user_service.pojos.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaretakerDelete {

    private Boolean status;
    private String message;
}
