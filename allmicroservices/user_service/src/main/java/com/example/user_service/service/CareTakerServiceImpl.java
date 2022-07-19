package com.example.user_service.service;


import com.example.user_service.exception.UserCaretakerException;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.Image;
import com.example.user_service.model.UserCaretaker;
import com.example.user_service.model.UserMedicines;
import com.example.user_service.pojos.response.NotificationMessage;
import com.example.user_service.pojos.request.UserCaretakerDTO;
import com.example.user_service.pojos.response.ImageResponse;
import com.example.user_service.pojos.response.CaretakerResponsePage;
import com.example.user_service.repository.ImageRepository;
import com.example.user_service.repository.UserCaretakerRepository;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.util.Datehelper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.example.user_service.util.Constants.*;
import static com.example.user_service.util.Constants.LoggerConstants.*;

/**
 * This class contains all the business logic for the caretaker controller
 */
@Service
public class CareTakerServiceImpl implements CareTakerService {


    private final UserCaretakerRepository userCaretakerRepository;
    private final ModelMapper mapper;
    private final ImageRepository imageRepository;
    private final UserMedicineRepository userMedicineRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${project.rabbitmq.exchange}")
    private String topicExchange;

    @Value("${project.rabbitmq.routingKeyNotify}")
    private String routingKeyNotify;

    Logger logger = LoggerFactory.getLogger(CareTakerServiceImpl.class);
    public CareTakerServiceImpl(ImageRepository imageRepository, UserMedicineRepository userMedicineRepository, RabbitTemplate rabbitTemplate, UserCaretakerRepository userCaretakerRepository, ModelMapper modelMapper){
        this.imageRepository=imageRepository;
        this.userMedicineRepository= userMedicineRepository;
        this.rabbitTemplate= rabbitTemplate;
        this.userCaretakerRepository= userCaretakerRepository;
        this.mapper= modelMapper;
    }
    @Override
    public UserCaretaker saveCareTaker(UserCaretakerDTO userCaretakerDTO) throws UserCaretakerException {

        logger.info(STARTING_METHOD_EXECUTION);
            UserCaretaker userCaretaker = mapToEntity(userCaretakerDTO);
            userCaretaker.setCreatedAt(Datehelper.getCurrentDataTime());
            if (userCaretakerRepository.check(userCaretaker.getPatientId(), userCaretaker.getCaretakerId()) != null) {
                throw new UserCaretakerException(CARETAKER_ALREADY_PRESENT);
            } else {
                logger.info(RESPONSE_SAVED);
                userCaretakerRepository.save(userCaretaker);
                logger.info(EXITING_METHOD_EXECUTION);
                return userCaretaker;
            }
    }

    @Override
    public UserCaretaker updateCaretakerStatus(String caretakerId) throws UserCaretakerException {
        logger.info(STARTING_METHOD_EXECUTION);
            UserCaretaker uc = userCaretakerRepository.getById(caretakerId);
            if (uc.getCaretakerId()==null) {
                logger.info("Caretaker not found with cId : {}",caretakerId);
                throw new UserCaretakerException(NO_RECORD_FOUND);
            }
            uc.setReqStatus(true);
        logger.info(RESPONSE_SAVED);
        userCaretakerRepository.save(uc);
        logger.info(EXITING_METHOD_EXECUTION);
        return uc;
    }

    @Override
    public CaretakerResponsePage getPatientsUnderMe(String userId, int pageNo, int pageSize) throws UserCaretakerException {

        logger.info(STARTING_METHOD_EXECUTION);
            Pageable pageable= PageRequest.of(pageNo,pageSize);
            Page<UserCaretaker> userCaretakerPage = userCaretakerRepository.getPatientsUnderMe(userId, pageable);
            if (userCaretakerPage.isEmpty()) {
                logger.debug("No patients found for userID : {}",userId);
                throw new UserCaretakerException(DATA_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return new CaretakerResponsePage(SUCCESS,DATA_FOUND,userCaretakerPage.getTotalElements(),userCaretakerPage.getTotalPages(),pageNo,userCaretakerPage.getContent());
    }

    @Override
    public List<UserCaretaker> getPatientRequests(String userId) throws UserCaretakerException {
        logger.info(STARTING_METHOD_EXECUTION);
            List<UserCaretaker> userCaretaker =userCaretakerRepository.getPatientRequests(userId);
            if (userCaretaker.isEmpty()) {
                throw new UserCaretakerException(DATA_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return userCaretaker;
    }

    @Override
    public List<UserCaretaker> getMyCaretakers(String userId) throws UserCaretakerException {
        logger.info(STARTING_METHOD_EXECUTION);
            List<UserCaretaker> userCaretaker = userCaretakerRepository.getMyCaretakers(userId);
            if (userCaretaker.isEmpty()) {
                throw new UserCaretakerException(DATA_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return userCaretaker;
    }

    @Override
    public List<UserCaretaker> getCaretakerRequestStatus(String userId) throws UserCaretakerException {

        logger.info(STARTING_METHOD_EXECUTION);
        List<UserCaretaker> userCaretakerList= userCaretakerRepository.getCaretakerRequestStatus(userId);
            if(userCaretakerList.isEmpty()){
                throw new UserCaretakerException(NO_RECORD_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return userCaretakerList;
    }


    @Override
    public List<UserCaretaker> getCaretakerRequests(String userId) throws UserCaretakerException {

        logger.info(STARTING_METHOD_EXECUTION);
        List<UserCaretaker> userCaretaker = userCaretakerRepository.getCaretakerRequests(userId);
            if (userCaretaker.isEmpty()) {
                throw new UserCaretakerException(DATA_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return userCaretaker;
    }

    @Override
    public String delPatientReq(String caretakerId) throws UserExceptionMessage, UserCaretakerException {

        logger.info(STARTING_METHOD_EXECUTION);
        Optional<UserCaretaker> userCaretaker = userCaretakerRepository.findById(caretakerId);
            if (userCaretaker.isPresent()) {
                userCaretaker.get().setDelete(true);
                logger.info(EXITING_METHOD_EXECUTION);
                return SUCCESS;

            }
            throw new UserCaretakerException(NO_RECORD_FOUND);
    }

    @Override
    public ImageResponse sendImageToCaretaker(MultipartFile multipartFile, String filename,  String medName,String caretakerid, Integer medId) {
        try {
            logger.info(STARTING_METHOD_EXECUTION);
            Path path = Paths.get(System.getProperty("user.dir") + "/src/main/upload/static/images", filename.concat(".").concat("jpg"));
            Files.write(path, multipartFile.getBytes());

            UserMedicines userMedicines = userMedicineRepository.getMedById(medId);
            if(userMedicines.getUser()==null){
                throw new UserCaretakerException(DATA_NOT_FOUND);
            }
            String userName = userMedicines.getUser().getUserName();
            Image image = new Image();
            image.setImageUrl(path.getFileName().toString());
            image.setTime(Calendar.getInstance().getTime().toString());
            image.setDate(Calendar.getInstance().getTime());
            image.setUserMedicines(userMedicines);
            image.setCaretakerName(userName);
            imageRepository.save(image);
            String fcmToken = "epkw4MI-RxyMzZjvD6fUl6:APA91bEUyAJpJ5RmDyI1KLcMLJbBPiYSX64oIW4WkNq62zeUlMPUPknGkBHTB_drOBX6CUkiI0Pyfc4Myvt87v6BU69kz0LPq4YM9iWnG9RrNbxIpC4LrtE-zWfNdbB3dbjR2bmogops";
            rabbitTemplate.convertAndSend(topicExchange, routingKeyNotify, new NotificationMessage(fcmToken, "Take medicine", "caretaker", medName, filename + ".jpg"));

        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ImageResponse(FAILED,e.getMessage());
        }
        logger.info(EXITING_METHOD_EXECUTION);
        return new ImageResponse(SUCCESS,SENT_SUCCESS);
    }

    private UserCaretaker mapToEntity(UserCaretakerDTO userCaretakerDTO) {

        logger.info(EXITING_METHOD_EXECUTION);
        return mapper.map(userCaretakerDTO, UserCaretaker.class);

    }

}