package com.example.user_service.controller;


import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.pojos.request.MedicineHistoryDTO;

import com.example.user_service.pojos.request.MedicinePojo;
import com.example.user_service.pojos.response.ImageListResponse;
import com.example.user_service.pojos.response.MedicineResponse;
import com.example.user_service.pojos.response.SyncResponse;
import com.example.user_service.service.UserMedicineService;
import com.example.user_service.util.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * This controller is used to create restful web services for user medicines
 */
@RestController
@RequestMapping(path = "/api/v1/")
@Validated
public class MedicineController {

    private final UserMedicineService userMedicineService;


    MedicineController(UserMedicineService userMedicineService){
        this.userMedicineService=userMedicineService;
    }

    /**
     * Syncs local storage data of the application with the server
     */
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @PostMapping(value = "/medicines/sync", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SyncResponse> syncData(@NotNull @NotBlank  @RequestParam("userId") String userId,@Valid @RequestBody List<MedicinePojo> medicinePojo) throws UserMedicineException{
            String message = userMedicineService.syncData(userId,medicinePojo);
            return new ResponseEntity<>(new SyncResponse(message, "Synced Successfully"), HttpStatus.OK);

    }

    /**
     * Syncs medicine history of all the medicines from local storage to backend
     */
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @PostMapping(value = "/medicine-history/sync",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SyncResponse> syncMedicineHistory(@NotNull @NotBlank @RequestParam(name = "medId") Integer medId,
                                                 @Valid @RequestBody List<MedicineHistoryDTO> medicineHistory) throws UserMedicineException {
            userMedicineService.syncMedicineHistory(medId, medicineHistory);
            return new ResponseEntity<>(new SyncResponse(Constants.SUCCESS, "Synced Successfully"), HttpStatus.OK);

    }


    /**
     * Fetch all medicines for a user by id
     */
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/medicine-histories")
    public ResponseEntity<MedicineResponse> getMedicineHistories(@NotNull @NotBlank @RequestParam(name = "medId") Integer medId) throws UserMedicineException {

     return new ResponseEntity<>(userMedicineService.getMedicineHistory(medId),HttpStatus.OK);


    }

    /**
     * Fetches all the images stored for that medicine
     */
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/medicine-images")
    public ResponseEntity<ImageListResponse> getMedicineImages(@NotNull @NotBlank @RequestParam(name = "medId") Integer medId){

        ImageListResponse imageListResponse= new ImageListResponse(Constants.SUCCESS, Constants.DATA_FOUND,userMedicineService.getUserMedicineImages(medId));
        return new ResponseEntity<>(imageListResponse,HttpStatus.OK);


    }
}