package com.example.user_service.pojos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a Pojo class for medicine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicinePojo {
    private int userId;
    private String days;
    private int currentCount;
    private String endDate;
    private String medicineDes;
    private int totalMedReminders;
    private String medicineName;
    private String title;
    private String startDate;
    private int status;
    private String time;

}
