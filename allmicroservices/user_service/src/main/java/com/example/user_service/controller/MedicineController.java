package com.example.user_service.controller;


import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.pojos.dto.medicine.MedicineHistoryDTO;

import com.example.user_service.pojos.dto.medicine.MedicinePojo;
import com.example.user_service.pojos.response.ImageListResponse;
import com.example.user_service.pojos.response.MedicineResponse;
import com.example.user_service.pojos.response.SyncResponse;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserMedicineService;
import com.example.user_service.util.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import java.util.stream.Collectors;

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
    @PostMapping(value = "/medicines/sync", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SyncResponse> syncData(@NotNull @NotBlank  @RequestParam("userId") String userId,@Valid @RequestBody List<MedicinePojo> medicinePojo) throws UserMedicineException {

        try {
            UserEntity userEntity = userRepository.getUserById(userId);

            List<UserMedicines> userMedicinesList = medicinePojo.stream().map(medicinePojo1 -> {
                        UserMedicines userMedicines = new UserMedicines();

                        userMedicines.setMedicineDes(medicinePojo1.getMedicineDes());
                        userMedicines.setMedicineName(medicinePojo1.getMedicineName());
                        userMedicines.setDays(medicinePojo1.getDays());
                        userMedicines.setMedicineId(medicinePojo1.getUserId());
                        userMedicines.setEndDate(medicinePojo1.getEndDate());
                        userMedicines.setTitle(medicinePojo1.getTitle());
                        userMedicines.setCurrentCount(medicinePojo1.getCurrentCount());
                        userMedicines.setTotalMedReminders(medicinePojo1.getTotalMedReminders());
                        userMedicines.setStartDate(medicinePojo1.getStartDate());
                        userMedicines.setTime(medicinePojo1.getTime());
                        userMedicines.setUserEntity(userEntity);

                        return userMedicines;
                    })
                    .collect(Collectors.toList());

            userMedicineRepository.saveAll(userMedicinesList);
            return new ResponseEntity<>(new SyncResponse(Messages.SUCCESS, "Synced Successfully"), HttpStatus.OK);
        }
        catch (Exception exception){
            throw new UserMedicineException("Sync failed");
        }

    }

    @PostMapping(value = "/medicine-history/sync")
    public ResponseEntity<SyncResponse> syncMedicineHistory(@NotNull @NotBlank @RequestParam(name = "medId") Integer medId,
                                                 @Valid @RequestBody List<MedicineHistoryDTO> medicineHistory) throws UserMedicineException {
        try {

            userMedicineService.syncMedicineHistory(medId, medicineHistory);
            return new ResponseEntity<>(new SyncResponse(Messages.SUCCESS, "Synced Successfully"), HttpStatus.OK);
        }catch (Exception e){
            throw new UserMedicineException("Sync failed");
        }

    }

    @GetMapping(value = "/medicine-histories")
    public ResponseEntity<MedicineResponse> getMedicineHistories(@NotNull @NotBlank @RequestParam(name = "medId") Integer medId) throws UserMedicineException {

     return new ResponseEntity<>(userMedicineService.getMedicineHistory(medId),HttpStatus.OK);


    }

    @GetMapping(value = "/medicine-images")
    public ResponseEntity<ImageListResponse> getMedicineImages(@NotNull @NotBlank @RequestParam(name = "medId") Integer medId){

        ImageListResponse imageListResponse= new ImageListResponse(Messages.SUCCESS,Messages.DATA_FOUND,userMedicineService.getUserMedicineImages(medId));
        return new ResponseEntity<>(imageListResponse,HttpStatus.OK);


    }
}