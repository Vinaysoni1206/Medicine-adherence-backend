package com.example.user_service.pojos.dto.medicine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineHistoryDTO {

    private int remId;

    @NotNull(message = "Date cannot be null")
    @NotBlank(message = "Date cannot be blank")
    private String date;

    @NotNull(message = "Timings cannot be null")
    @NotEmpty(message = "Timings cannot be empty")
    private String[] taken;

    @NotNull(message = "Timings cannot be null")
    @NotEmpty(message = "Timings cannot be empty")
    private String[] notTaken;

}
//