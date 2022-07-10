package com.example.user_service.service.medicine;

import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.model.image.Image;
import com.example.user_service.model.medicine.MedicineHistory;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.pojos.dto.medicine.MedicineHistoryDTO;
import com.example.user_service.pojos.dto.medicine.MedicinePojo;
import com.example.user_service.pojos.response.MedicineResponse;
import com.example.user_service.repository.ImageRepository;
import com.example.user_service.repository.UserMedHistoryRepository;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class UserMedicineServiceImpl implements UserMedicineService {

    @Autowired
    UserRepository userRepository;

    UserMedicineRepository userMedicineRepository;

    ImageRepository imageRepository;

    UserMedHistoryRepository userMedHistoryRepository;

    Logger logger = LoggerFactory.getLogger(UserMedicineServiceImpl.class);

    public UserMedicineServiceImpl(UserRepository userRepository, UserMedicineRepository userMedicineRepository, ImageRepository imageRepository, UserMedHistoryRepository userMedHistoryRepository) {
        this.userRepository=userRepository;
        this.userMedicineRepository=userMedicineRepository;
        this.imageRepository=imageRepository;
        this.userMedHistoryRepository= userMedHistoryRepository;
    }


    @Override
    @Async
    public CompletableFuture<List<UserMedicines>> getallUserMedicines(String userId) throws UserMedicineException, UserExceptionMessage {
            UserEntity user = userRepository.getUserById(userId);
            if (user == null) {
                throw new UserExceptionMessage(Messages.DATA_NOT_FOUND);
            }
            List<UserMedicines> list = user.getUserMedicines();
            return CompletableFuture.completedFuture(list);


    }


    @Override
    public String syncData(String userId, List<MedicinePojo> medicinePojo) throws UserMedicineException {
            UserEntity user = userRepository.getUserById(userId);
            if (user.getUserMedicines().isEmpty()) {
                throw new UserMedicineException("Unable to sync");
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

        userMedicineRepository.saveAll(userMedicinesList);
            return Messages.SUCCESS;

    }


    @Override

    public MedicineResponse syncMedicineHistory(Integer medId, List<MedicineHistoryDTO> medicineHistoryDTOS) throws UserMedicineException {
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
        return new MedicineResponse(Messages.SUCCESS,Messages.DATA_FOUND,medicineHistories);

    }

    @Override
    public MedicineResponse getMedicineHistory(Integer medId) throws UserMedicineException {

            List<MedicineHistory> medicineHistories = userMedicineRepository.getMedById(medId).getMedicineHistories();
            if (medicineHistories.isEmpty()) {
                throw new UserMedicineException("No record found!!");
            }
            return new MedicineResponse("OK", "Medicine History", medicineHistories);

    }

    @Override
    public List<Image> getUserMedicineImages(Integer medId) {
            return userMedicineRepository.getMedById(medId)
                    .getImages()
                    .stream()
                    .sorted(Comparator.comparing(Image::getDate).reversed())
                    .collect(Collectors.toList());

    }

}
