package com.example.user_service.service.medicine;

import com.example.user_service.exception.user.UserExceptionMessage;
import com.example.user_service.exception.medicine.UserMedicineException;
import com.example.user_service.model.image.Image;
import com.example.user_service.model.medicine.MedicineHistory;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.pojos.dto.medicine.MedicineHistoryDTO;
import com.example.user_service.pojos.dto.medicine.MedicinePojo;
import com.example.user_service.pojos.response.medicine.MedicineResponse;
import com.example.user_service.repository.ImageRepository;
import com.example.user_service.repository.UserMedHistoryRepository;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.example.user_service.util.Messages.*;
import static com.example.user_service.util.Messages.LoggerConstants.*;

@Service
public class UserMedicineServiceImpl implements UserMedicineService {

    final UserRepository userRepository;

    final UserMedicineRepository userMedicineRepository;

    final ImageRepository imageRepository;

    final UserMedHistoryRepository userMedHistoryRepository;

    Logger logger = LoggerFactory.getLogger(UserMedicineServiceImpl.class);

    public UserMedicineServiceImpl(UserRepository userRepository, UserMedicineRepository userMedicineRepository, ImageRepository imageRepository, UserMedHistoryRepository userMedHistoryRepository) {
        this.userRepository=userRepository;
        this.userMedicineRepository=userMedicineRepository;
        this.imageRepository=imageRepository;
        this.userMedHistoryRepository= userMedHistoryRepository;
    }


    @Override
    @Async
    public CompletableFuture<List<UserMedicines>> getAllUserMedicines(String userId) throws UserMedicineException, UserExceptionMessage {
        logger.info(STARTING_METHOD_EXECUTION);
        UserEntity user = userRepository.getUserById(userId);
            if (user == null) {
                throw new UserExceptionMessage(DATA_NOT_FOUND);
            }
            List<UserMedicines> list = user.getUserMedicines();
            logger.info(EXITING_METHOD_EXECUTION);
            return CompletableFuture.completedFuture(list);


    }


    @Override
    public String syncData(String userId, List<MedicinePojo> medicinePojo) throws UserMedicineException {

        logger.info(STARTING_METHOD_EXECUTION);
        UserEntity user = userRepository.getUserById(userId);
            if (user.getUserMedicines().isEmpty()) {
                throw new UserMedicineException(UNABLE_TO_SYNC);
            }
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
                    userMedicines.setUserEntity(user);
                    return userMedicines;
                })
                .collect(Collectors.toList());
        logger.info(RESPONSE_SAVED);
        userMedicineRepository.saveAll(userMedicinesList);
        logger.info(EXITING_METHOD_EXECUTION);
        return Messages.SUCCESS;

    }


    @Override

    public MedicineResponse syncMedicineHistory(Integer medId, List<MedicineHistoryDTO> medicineHistoryDTOS) throws UserMedicineException {

        logger.info(STARTING_METHOD_EXECUTION);
        UserMedicines userMedicines = userMedicineRepository.getMedById(medId);
            if (userMedicines == null) {
                throw new UserMedicineException("Unable to sync");

            }
            List<MedicineHistory> medicineHistories = medicineHistoryDTOS.stream().map(medHid -> {
                MedicineHistory medicineHistory1 = new MedicineHistory();
                medicineHistory1.setHistoryId(medHid.getRemId());
                medicineHistory1.setDate(medHid.getDate());
                medicineHistory1.setTaken(String.join(",", medHid.getTaken()));
                medicineHistory1.setNotTaken(String.join(",", medHid.getNotTaken()));
                medicineHistory1.setUserMedicines(userMedicines);
                return medicineHistory1;
            }).collect(Collectors.toList());
            CompletableFuture.completedFuture(userMedHistoryRepository.saveAll(medicineHistories));
        logger.info(EXITING_METHOD_EXECUTION);
        return new MedicineResponse(SUCCESS,DATA_FOUND,medicineHistories);

    }

    @Override
    public MedicineResponse getMedicineHistory(Integer medId) throws UserMedicineException {
        logger.info(STARTING_METHOD_EXECUTION);
        List<MedicineHistory> medicineHistories = userMedicineRepository.getMedById(medId).getMedicineHistories();
            if (medicineHistories.isEmpty()) {
                throw new UserMedicineException(NO_RECORD_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return new MedicineResponse("OK", "Medicine History", medicineHistories);

    }

    @Override
    public List<Image> getUserMedicineImages(Integer medId) {

        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return userMedicineRepository.getMedById(medId)
                    .getImages()
                    .stream()
                    .sorted(Comparator.comparing(Image::getDate).reversed())
                    .collect(Collectors.toList());

    }

}
