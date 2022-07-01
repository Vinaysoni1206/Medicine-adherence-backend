package com.example.user_service.service;


import com.example.user_service.exception.DataAccessExceptionMessage;
import com.example.user_service.exception.UserCaretakerException;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.image.Image;
import com.example.user_service.model.user.UserCaretaker;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.pojos.Notificationmessage;
import com.example.user_service.pojos.dto.UserCaretakerDTO;
import com.example.user_service.pojos.response.ImageResponse;
import com.example.user_service.pojos.response.responsepages.CaretakerResponsePage;
import com.example.user_service.repository.ImageRepository;
import com.example.user_service.repository.UserCaretakerRepository;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.util.Datehelper;
import com.example.user_service.util.Messages;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class CareTakerServiceImpl implements CareTakerService {


    private final UserCaretakerRepository userCaretakerRepository;
    private final ModelMapper mapper;
    private final ImageRepository imageRepository;
    private final UserMedicineRepository userMedicineRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${project.rabbitmq.exchange}")
    private String topicExchange;

    @Value("${project.rabbitmq.routingkey2}")
    private String routingKey2;

    Logger logger = LoggerFactory.getLogger(CareTakerServiceImpl.class);
    CareTakerServiceImpl(ImageRepository imageRepository, UserMedicineRepository userMedicineRepository, RabbitTemplate rabbitTemplate, UserCaretakerRepository userCaretakerRepository, ModelMapper modelMapper){
        this.imageRepository=imageRepository;
        this.userMedicineRepository= userMedicineRepository;
        this.rabbitTemplate= rabbitTemplate;
        this.userCaretakerRepository= userCaretakerRepository;
        this.mapper= modelMapper;
    }
    @Override
    public UserCaretaker saveCareTaker(UserCaretakerDTO userCaretakerDTO) throws UserCaretakerException {

        try {
            UserCaretaker userCaretaker = mapToEntity(userCaretakerDTO);
            userCaretaker.setCreatedAt(Datehelper.getcurrentdatatime());
            if (userCaretakerRepository.check(userCaretaker.getPatientId(), userCaretaker.getCaretakerId()) != null) {
                throw new UserCaretakerException("Caretaker Already Present!!");
            } else {
                return userCaretakerRepository.save(userCaretaker);
            }
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }
    }

    @Override
    public UserCaretaker updateCaretakerStatus(String cId) throws UserCaretakerException {
        try {
            Optional<UserCaretaker> uc = userCaretakerRepository.findById(cId);
            if (uc.isEmpty()) {
                throw new UserCaretakerException("Request not found");
            }
            uc.get().setReqStatus(true);
            return userCaretakerRepository.save(uc.get());
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }
    }

    @Override
    public CaretakerResponsePage getPatientsUnderMe(String userId, int pageNo, int pageSize) throws UserCaretakerException {

        try {
            Pageable pageable= PageRequest.of(pageNo,pageSize);
            Page<UserCaretaker> userCaretakerPage = userCaretakerRepository.getPatientsUnderMe(userId, pageable);
            if (userCaretakerPage.isEmpty()) {
                throw new UserCaretakerException(Messages.DATA_NOT_FOUND);
            }
            return new CaretakerResponsePage(Messages.SUCCESS,Messages.DATA_FOUND,userCaretakerPage.getTotalElements(),userCaretakerPage.getTotalPages(),pageNo,userCaretakerPage.getContent());
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }
    }

    @Override
    public List<UserCaretaker> getPatientRequests(String userId) throws UserCaretakerException {
        try {
            List<UserCaretaker> userCaretaker =userCaretakerRepository.getPatientRequests(userId);
            if (userCaretaker.isEmpty()) {
                throw new UserCaretakerException(Messages.DATA_NOT_FOUND);
            }
            return userCaretaker;
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }
    }

    @Override
    public List<UserCaretaker> getMyCaretakers(String userId) throws UserCaretakerException {
        try {
            List<UserCaretaker> userCaretaker = userCaretakerRepository.getMyCaretakers(userId);
            if (userCaretaker.isEmpty()) {
                throw new UserCaretakerException(Messages.DATA_NOT_FOUND);
            }
            return userCaretaker;
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }
    }

    @Override
    public List<UserCaretaker> getCaretakerRequestStatus(String userId) {
        try {
            return userCaretakerRepository.getCaretakerRequestStatus(userId);
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }
    }


    @Override
    public List<UserCaretaker> getCaretakerRequestsP(String userId) throws UserCaretakerException {
        try {
            List<UserCaretaker> userCaretaker = userCaretakerRepository.getCaretakerRequestsP(userId);
            if (userCaretaker.isEmpty()) {
                throw new UserCaretakerException(Messages.DATA_NOT_FOUND);
            }
            return userCaretaker;
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR + dataAccessException.getMessage());
        }
    }

    @Override
    public String delPatientReq(String cId) throws UserExceptionMessage, UserCaretakerException {


        try {
            Optional<UserCaretaker> userCaretaker = userCaretakerRepository.findById(cId);
            if (userCaretaker.isPresent()) {
                userCaretakerRepository.delete(userCaretaker.get());
                return Messages.SUCCESS;

            }
            throw new UserCaretakerException(Messages.NO_RECORD_FOUND);
        } catch (Exception e) {
            throw new UserCaretakerException(Messages.NO_RECORD_FOUND);
        }
    }

    @Override
    public ImageResponse sendImageToCaretaker(MultipartFile multipartFile, String filename, String caretakerid, String medName, Integer medId) throws IOException, UserCaretakerException {

        try {
            File file = new File(System.getProperty("user.dir") + "/src/main/upload/static/images");
            if (!file.exists()) {
                file.mkdir();
            }
            Path path = Paths.get(System.getProperty("user.dir") + "/src/main/upload/static/images", filename.concat(".").concat("jpg"));
            Files.write(path, multipartFile.getBytes());

            UserMedicines userMedicines = userMedicineRepository.getMedById(medId);
            String userName = userMedicines.getUserEntity().getUserName();
            Image image = new Image();
            image.setImageUrl(path.getFileName().toString());
            image.setTime(Calendar.getInstance().getTime().toString());
            image.setDate(Calendar.getInstance().getTime());
            image.setUserMedicines(userMedicines);
            image.setCaretakerName(userName);
            imageRepository.save(image);
            String fcmToken = "epkw4MI-RxyMzZjvD6fUl6:APA91bEUyAJpJ5RmDyI1KLcMLJbBPiYSX64oIW4WkNq62zeUlMPUPknGkBHTB_drOBX6CUkiI0Pyfc4Myvt87v6BU69kz0LPq4YM9iWnG9RrNbxIpC4LrtE-zWfNdbB3dbjR2bmogops";
            rabbitTemplate.convertAndSend(topicExchange, routingKey2, new Notificationmessage(fcmToken, "Take medicine", "caretaker", medName, filename + ".jpg"));

        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ImageResponse(Messages.FAILED,Messages.UNABLE_TO_SEND);
        }

        return new ImageResponse(Messages.SUCCESS,Messages.SENT_SUCCESS);
    }

    private UserCaretaker mapToEntity(UserCaretakerDTO userCaretakerDTO) {
        return mapper.map(userCaretakerDTO, UserCaretaker.class);

    }

///

}