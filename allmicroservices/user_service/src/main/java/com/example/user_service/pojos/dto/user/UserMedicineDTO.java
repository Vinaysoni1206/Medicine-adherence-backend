package com.example.user_service.pojos.dto.user;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserMedicineDTO {
    private String medicineName;
    private String title;
    private String medicineDes;
    private String startDate;
    private String endDate;
    private String time;
    private String days;

    public UserMedicineDTO(String medicineName, String title, String medicineDes, String startDate,
                           String endDate, String time, String days){
        this.medicineName = medicineName;
        this.title= title;
        this.medicineDes = medicineDes;
        this.startDate= startDate;
        this.endDate = endDate;
        this.time = time;
        this.days = days;
    }

}