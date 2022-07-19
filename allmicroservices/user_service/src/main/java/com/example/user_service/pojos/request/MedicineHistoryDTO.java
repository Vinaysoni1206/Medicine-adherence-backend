package com.example.user_service.pojos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * This is a Pojo class for Medicine History
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineHistoryDTO {

    private int remId;
    private String date;
    private String[] taken;
    private String[] notTaken;
}