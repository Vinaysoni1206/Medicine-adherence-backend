package com.example.user_service.service;

import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.model.Image;
import com.example.user_service.model.UserMedicines;
import com.example.user_service.pojos.request.MedicineHistoryDTO;
import com.example.user_service.pojos.request.MedicineDTO;
import com.example.user_service.pojos.response.MedicineResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This is an interface for user medicine service
 */
public interface UserMedicineService {


    CompletableFuture<List<UserMedicines>> getAllUserMedicines(String userId) throws UserMedicineException, UserExceptionMessage;

    String syncData(String userId , List<MedicineDTO> list) throws UserMedicineException;

    MedicineResponse syncMedicineHistory(Integer medicineId , List<MedicineHistoryDTO> medicineHistoryDTOS) throws UserMedicineException;

    MedicineResponse getMedicineHistory(Integer medicineId) throws UserMedicineException;

    List<Image> getUserMedicineImages(Integer medicineId);

}