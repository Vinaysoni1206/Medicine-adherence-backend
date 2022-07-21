package com.example.user_service.controller;


import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.pojos.request.MedicineHistoryDTO;

import com.example.user_service.pojos.request.MedicineDTO;
import com.example.user_service.pojos.response.ImageListResponse;
import com.example.user_service.pojos.response.MedicineResponse;
import com.example.user_service.pojos.response.SyncResponse;
import com.example.user_service.service.UserMedicineService;
import com.example.user_service.util.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

import static com.example.user_service.util.Constants.*;


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
    @ApiOperation(value = "Syncs local storage data of the application with the server")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = SUCCESS),
            @ApiResponse(code = 500,message = "Unable to sync, medicine not found"),
            @ApiResponse(code = 401, message = UNAUTHORIZED)})
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @PostMapping(value = "/medicines/sync", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SyncResponse> syncData(@NotNull @NotBlank  @RequestParam("userId") String userId,@Valid @RequestBody List<MedicineDTO> medicinePojo) throws UserMedicineException{
            String message = userMedicineService.syncData(userId,medicinePojo);
            return new ResponseEntity<>(new SyncResponse(message, "Synced Successfully"), HttpStatus.OK);

    }

    /**
     * Syncs medicine history of all the medicines from local storage to backend
     */
    @ApiOperation(value = "Syncs medicine history of all the medicines from local storage to backend")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = SUCCESS),
            @ApiResponse(code = 500,message = "Unable to sync , medicine list empty"),
            @ApiResponse(code = 401, message = UNAUTHORIZED)})
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @PostMapping(value = "/medicine-history/sync",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SyncResponse> syncMedicineHistory(@NotNull @NotBlank @RequestParam(name = "medicineId") Integer medicineId,
                                                 @Valid @RequestBody List<MedicineHistoryDTO> medicineHistory) throws UserMedicineException {
            userMedicineService.syncMedicineHistory(medicineId, medicineHistory);
            return new ResponseEntity<>(new SyncResponse(Constants.SUCCESS, "Synced Successfully"), HttpStatus.OK);

    }


    /**
     * Fetch all medicines for a user by id
     */
    @ApiOperation(value = "Fetch all medicines for a user by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = SUCCESS),
            @ApiResponse(code = 500,message = MEDICINES_NOT_FOUND),
            @ApiResponse(code = 401, message = UNAUTHORIZED)})
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/medicine-histories")
    public ResponseEntity<MedicineResponse> getMedicineHistories(@NotNull @NotBlank @RequestParam(name = "medicineId") Integer medicineId) throws UserMedicineException {

     return new ResponseEntity<>(userMedicineService.getMedicineHistory(medicineId),HttpStatus.OK);


    }

    /**
     * Fetches all the images stored for that medicine
     */
    @ApiOperation(value = "Fetches all the images stored for that medicine")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = SUCCESS),
            @ApiResponse(code = 401, message = UNAUTHORIZED)})
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/medicine-images")
    public ResponseEntity<ImageListResponse> getMedicineImages(@NotNull @NotBlank @RequestParam(name = "medicineId") Integer medicineId){

        ImageListResponse imageListResponse= new ImageListResponse(Constants.SUCCESS, Constants.DATA_FOUND,userMedicineService.getUserMedicineImages(medicineId));
        return new ResponseEntity<>(imageListResponse,HttpStatus.OK);


    }
}