package com.example.user_service.service.medicine;

import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.model.image.Image;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.pojos.dto.medicine.MedicineHistoryDTO;
import com.example.user_service.pojos.dto.medicine.MedicinePojo;
import com.example.user_service.pojos.response.MedicineResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserMedicineService {


    CompletableFuture<List<UserMedicines>> getAllUserMedicines(String userId) throws UserMedicineException, UserExceptionMessage;

    String syncData(String userId , List<MedicinePojo> list) throws UserMedicineException;

    MedicineResponse syncMedicineHistory(Integer medId , List<MedicineHistoryDTO> medicineHistoryDTOS) throws UserMedicineException;

    MedicineResponse getMedicineHistory(Integer medId) throws UserMedicineException;

    List<Image> getUserMedicineImages(Integer medId);

}