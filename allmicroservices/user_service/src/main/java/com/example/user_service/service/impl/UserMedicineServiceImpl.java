package com.example.user_service.service.impl;

import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.model.Image;
import com.example.user_service.model.MedicineHistory;
import com.example.user_service.model.User;
import com.example.user_service.model.UserMedicines;
import com.example.user_service.pojos.request.MedicineHistoryDTO;
import com.example.user_service.pojos.request.MedicineDTO;
import com.example.user_service.pojos.response.MedicineResponse;
import com.example.user_service.repository.ImageRepository;
import com.example.user_service.repository.UserMedHistoryRepository;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserMedicineService;
import com.example.user_service.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.example.user_service.util.Constants.*;
import static com.example.user_service.util.Constants.LoggerConstants.*;

/**
 * This class contains all the business logic for the medicine controller
 */
@Service
public class UserMedicineServiceImpl implements UserMedicineService {

    final UserRepository userRepository;

    final UserMedicineRepository userMedicineRepository;

    final ImageRepository imageRepository;

    final UserMedHistoryRepository userMedicineHistoryRepository;

    Logger logger = LoggerFactory.getLogger(UserMedicineServiceImpl.class);

    public UserMedicineServiceImpl(UserRepository userRepository, UserMedicineRepository userMedicineRepository, ImageRepository imageRepository, UserMedHistoryRepository userMedHistoryRepository) {
        this.userRepository=userRepository;
        this.userMedicineRepository=userMedicineRepository;
        this.imageRepository=imageRepository;
        this.userMedicineHistoryRepository = userMedHistoryRepository;
    }


    @Override
    @Async
    public CompletableFuture<List<UserMedicines>> getAllUserMedicines(String userId) throws UserExceptionMessage {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Fetching list of medicines for user with id : {}",userId);
        User user = userRepository.getUserById(userId);
            if (user == null) {
                logger.debug("User not found with id : {}",userId);
                throw new UserExceptionMessage(USER_NOT_FOUND);
            }
            List<UserMedicines> list = user.getUserMedicines();
            logger.info(EXITING_METHOD_EXECUTION);
            return CompletableFuture.completedFuture(list);


    }


    @Override
    public String syncData(String userId, List<MedicineDTO> medicinePojo) throws UserMedicineException {

        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Syncing data for user with id : {}",userId);
        User user = userRepository.getUserById(userId);
            if (user.getUserMedicines().isEmpty()) {
                logger.debug("Unable to sync, medicine not found");
                throw new UserMedicineException(UNABLE_TO_SYNC);
            }
            List<UserMedicines> userMedicinesList = medicinePojo.stream().map(medicinePojo1 -> {
                    UserMedicines userMedicines = new UserMedicines();

                    userMedicines.setMedicineDescription(medicinePojo1.getMedicineDescription());
                    userMedicines.setMedicineName(medicinePojo1.getMedicineName());
                    userMedicines.setDays(medicinePojo1.getDays());
                    userMedicines.setMedicineId(medicinePojo1.getUserId());
                    userMedicines.setEndDate(medicinePojo1.getEndDate());
                    userMedicines.setTitle(medicinePojo1.getTitle());
                    userMedicines.setCurrentCount(medicinePojo1.getCurrentCount());
                    userMedicines.setTotalMedReminders(medicinePojo1.getTotalMedReminders());
                    userMedicines.setStartDate(medicinePojo1.getStartDate());
                    userMedicines.setTime(medicinePojo1.getTime());
                    userMedicines.setUser(user);
                    return userMedicines;
                })
                .collect(Collectors.toList());
        logger.info(RESPONSE_SAVED);
        userMedicineRepository.saveAll(userMedicinesList);
        logger.info(EXITING_METHOD_EXECUTION);
        return Constants.SUCCESS;

    }


    @Override

    public MedicineResponse syncMedicineHistory(Integer medicineId, List<MedicineHistoryDTO> medicineHistoryDTOS) throws UserMedicineException {

        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Syncing medicine history for user with medicineId : {}",medicineId);
        UserMedicines userMedicines = userMedicineRepository.getMedById(medicineId);
            if (userMedicines == null) {
                logger.debug("Unable to sync , medicine list empty");
                throw new UserMedicineException(UNABLE_TO_SYNC);

            }
            List<MedicineHistory> medicineHistories = medicineHistoryDTOS.stream().map(medicineHistoryId -> {
                MedicineHistory medicineHistory1 = new MedicineHistory();
                medicineHistory1.setHistoryId(medicineHistoryId.getReminderId());
                medicineHistory1.setDate(medicineHistoryId.getDate());
                medicineHistory1.setTaken(String.join(",", medicineHistoryId.getTaken()));
                medicineHistory1.setNotTaken(String.join(",", medicineHistoryId.getNotTaken()));
                medicineHistory1.setUserMedicines(userMedicines);
                return medicineHistory1;
            }).collect(Collectors.toList());
            CompletableFuture.completedFuture(userMedicineHistoryRepository.saveAll(medicineHistories));
        logger.info(EXITING_METHOD_EXECUTION);
        return new MedicineResponse(SUCCESS,DATA_FOUND,medicineHistories);

    }

    @Override
    public MedicineResponse getMedicineHistory(Integer medicineId) throws UserMedicineException {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Fetching medicine history with medicineId : {}", medicineId);
        List<MedicineHistory> medicineHistories = userMedicineRepository.getMedById(medicineId).getMedicineHistories();
            if (medicineHistories.isEmpty()) {
                logger.debug("Medicines not found");
                throw new UserMedicineException(MEDICINES_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return new MedicineResponse("OK", "Medicine History", medicineHistories);

    }

    @Override
    public List<Image> getUserMedicineImages(Integer medicineId) {

        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Fetching medicine images for a user with medicineId : {}", medicineId);
        logger.info(EXITING_METHOD_EXECUTION);
        return userMedicineRepository.getMedById(medicineId)
                    .getImages()
                    .stream()
                    .sorted(Comparator.comparing(Image::getDate).reversed())
                    .collect(Collectors.toList());

    }

}
