package com.example.user_service.pojos.response;


import com.example.user_service.model.MedicineHistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This is a response class for list of medicine
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineResponse {
    private String status;
    private String message;
    private List<MedicineHistory> userMedicinesList;

}
