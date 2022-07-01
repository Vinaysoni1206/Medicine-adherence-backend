package com.example.user_service.pojos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCaretakerDTO {

    @NotNull(message = "Patient name cannot be empty")
    @NotEmpty(message = "Patient name cannot be empty")
    private String patientName;

    private Boolean reqStatus;

    private String caretakerId;

    private String patientId;
    @NotNull(message = "Caretaker name cannot be empty")
    @NotEmpty(message = "Caretaker name cannot be empty")
    private String caretakerUsername;

    @NotNull(message = "Creation date cannot be empty")
    @NotEmpty(message = "Creation date cannot be empty")
    private String createdAt;

    @NotNull(message = "Sent by cannot be empty")
    @NotEmpty(message = "Sent by cannot be empty")
    private String sentBy;

}
//