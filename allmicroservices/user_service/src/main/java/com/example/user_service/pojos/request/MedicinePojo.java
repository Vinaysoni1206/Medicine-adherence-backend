package com.example.user_service.pojos.request;

import com.example.user_service.annotations.EmptyNotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.user_service.util.Constants.NullEmptyConstants.*;

/**
 * This is a Pojo class for medicine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicinePojo {

    @EmptyNotNull(message = ID_EMPTY_NULL)
    private int userId;

    @EmptyNotNull(message = DAYS_NULL_EMPTY)
    private String days;
    private int currentCount;

    @EmptyNotNull(message = DATE_NULL_EMPTY)
    private String endDate;

    @EmptyNotNull(message = MED_DES_EMPTY_NULL)
    private String medicineDescription;

    @EmptyNotNull(message = TOTAL_REMINDERS_NULL_EMPTY)
    private int totalMedReminders;

    @EmptyNotNull(message = MED_NAME_EMPTY_NULL)
    private String medicineName;

    @EmptyNotNull(message =TITLE_EMPTY_NULL)
    private String title;

    @EmptyNotNull(message = DATE_NULL_EMPTY)
    private String startDate;

    private int status;

    @EmptyNotNull(message = TIME_NULL)
    private String time;

}
