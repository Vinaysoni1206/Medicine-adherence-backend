package com.example.user_service.pojos.request;

import com.example.user_service.annotations.EmptyNotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import static com.example.user_service.util.Constants.NullEmptyConstants.ID_EMPTY_NULL;
import static com.example.user_service.util.Constants.NullEmptyConstants.MED_NAME_EMPTY_NULL;

/**
 * This is a Pojo class for Image
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendImageDto {

    private MultipartFile image;
    @EmptyNotNull()
    private String name;
    @EmptyNotNull(message = MED_NAME_EMPTY_NULL)
    private String medicineName;
    @EmptyNotNull(message = ID_EMPTY_NULL)
    private String id;
    @EmptyNotNull(message = ID_EMPTY_NULL)
    private Integer medicineId;
}
