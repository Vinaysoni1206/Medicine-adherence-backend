package com.example.user_service.pojos.request;


import com.example.user_service.annotations.EmptyNotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.user_service.util.Constants.NullEmptyConstants.*;

/**
 * This is a pojo class for user medicines
 */
@Data
@NoArgsConstructor
public class UserMedicineDTO {

    @EmptyNotNull(message = MED_NAME_EMPTY_NULL)
    private String medicineName;
    @EmptyNotNull(message =TITLE_EMPTY_NULL)
    private String title;
    @EmptyNotNull(message = MED_DES_EMPTY_NULL)
    private String medicineDescription;
    @EmptyNotNull(message = DATE_NULL_EMPTY)
    private String startDate;

    @EmptyNotNull(message = DATE_NULL_EMPTY)
    private String endDate;
    @EmptyNotNull(message = TIME_NULL)
    private String time;
    @EmptyNotNull(message = DAYS_NULL_EMPTY)
    private String days;

    public UserMedicineDTO(String medicineName, String title, String medicineDes, String startDate,
                           String endDate, String time, String days){
        this.medicineName = medicineName;
        this.title= title;
        this.medicineDescription = medicineDes;
        this.startDate= startDate;
        this.endDate = endDate;
        this.time = time;
        this.days = days;
    }

}