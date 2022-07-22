package com.example.user_service.pojos.request;

import com.example.user_service.annotations.EmptyNotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.user_service.util.Constants.NullEmptyConstants.*;


/**
 * This is pojo for User caretaker
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCaretakerDTO {
    @EmptyNotNull(message = PATIENT_NAME_EMPTY_NULL)
    private String patientName;
    private Boolean requestStatus;
    @EmptyNotNull(message = CARETAKER_ID_EMPTY_NULL)
    private String caretakerId;
    @EmptyNotNull(message = PATIENT_ID_EMPTY_NULL)
    private String patientId;
    @EmptyNotNull(message = CARETAKER_NAME_EMPTY)
    private String caretakerUsername;
    private String createdAt;
    @EmptyNotNull(message = SENT_BY_EMPTY_NULL)
    private String sentBy;

}
