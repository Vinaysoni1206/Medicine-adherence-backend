package com.example.user_service.pojos.request;

import com.example.user_service.annotations.EmptyNotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.user_service.util.Constants.NullEmptyConstants.*;


/**
 * This is a Pojo class for Medicine History
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineHistoryDTO {

    @EmptyNotNull(message = ID_EMPTY_NULL)
    private int reminderId;

    @EmptyNotNull(message = DATE_NULL_EMPTY)
    private String date;
    private String[] taken;
    private String[] notTaken;
}