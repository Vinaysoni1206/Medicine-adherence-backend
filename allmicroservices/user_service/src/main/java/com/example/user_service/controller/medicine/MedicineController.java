package com.example.user_service.controller.medicine;


import com.example.user_service.exception.medicine.UserMedicineException;
import com.example.user_service.pojos.dto.medicine.MedicineHistoryDTO;

import com.example.user_service.pojos.dto.medicine.MedicinePojo;
import com.example.user_service.pojos.response.image.ImageListResponse;
import com.example.user_service.pojos.response.medicine.MedicineResponse;
import com.example.user_service.pojos.response.SyncResponse;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.medicine.UserMedicineService;
import com.example.user_service.util.Messages;
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


@RestController
@RequestMapping(path = "/api/v1/")
@Validated
public class MedicineController {

    private final UserMedicineService userMedicineService;
    UserRepository userRepository;
    UserMedicineRepository userMedicineRepository;

    MedicineController(UserMedicineService userMedicineService, UserRepository userRepository, UserMedicineRepository userMedicineRepository){
        this.userMedicineRepository= userMedicineRepository;
        this.userRepository= userRepository;
        this.userMedicineService=userMedicineService;
    }

    // save caretaker for a patients
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @PostMapping(value = "/medicines/sync", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SyncResponse> syncData(@NotNull @NotBlank  @RequestParam("userId") String userId,@Valid @RequestBody List<MedicinePojo> medicinePojo) throws UserMedicineException{
            String message = userMedicineService.syncData(userId,medicinePojo);
            return new ResponseEntity<>(new SyncResponse(message, "Synced Successfully"), HttpStatus.OK);

    }

    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @PostMapping(value = "/medicine-history/sync",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SyncResponse> syncMedicineHistory(@NotNull @NotBlank @RequestParam(name = "medId") Integer medId,
                                                 @Valid @RequestBody List<MedicineHistoryDTO> medicineHistory) throws UserMedicineException {
            userMedicineService.syncMedicineHistory(medId, medicineHistory);
            return new ResponseEntity<>(new SyncResponse(Messages.SUCCESS, "Synced Successfully"), HttpStatus.OK);

    }

    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/medicine-histories")
    public ResponseEntity<MedicineResponse> getMedicineHistories(@NotNull @NotBlank @RequestParam(name = "medId") Integer medId) throws UserMedicineException {

     return new ResponseEntity<>(userMedicineService.getMedicineHistory(medId),HttpStatus.OK);


    }

    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/medicine-images")
    public ResponseEntity<ImageListResponse> getMedicineImages(@NotNull @NotBlank @RequestParam(name = "medId") Integer medId){

        ImageListResponse imageListResponse= new ImageListResponse(Messages.SUCCESS,Messages.DATA_FOUND,userMedicineService.getUserMedicineImages(medId));
        return new ResponseEntity<>(imageListResponse,HttpStatus.OK);


    }
}