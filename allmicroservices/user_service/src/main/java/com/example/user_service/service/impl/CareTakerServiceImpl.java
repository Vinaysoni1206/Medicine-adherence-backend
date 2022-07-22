package com.example.user_service.service.impl;


import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.exception.UserCaretakerException;
import com.example.user_service.model.Image;
import com.example.user_service.model.UserCaretaker;
import com.example.user_service.model.UserMedicines;
import com.example.user_service.pojos.response.*;
import com.example.user_service.pojos.request.UserCaretakerDTO;
import com.example.user_service.repository.ImageRepository;
import com.example.user_service.repository.UserCaretakerRepository;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.service.CareTakerService;
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
    public CaretakerResponse saveCareTaker(UserCaretakerDTO userCaretakerDTO) throws UserCaretakerException {

        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Saving a caretaker with details: {}", userCaretakerDTO);
            UserCaretaker userCaretaker = mapToEntity(userCaretakerDTO);
            userCaretaker.setCreatedAt(Datehelper.getCurrentDataTime());
            if (userCaretakerRepository.check(userCaretaker.getPatientId(), userCaretaker.getCaretakerId()) != null) {
                logger.debug("Caretaker already added");
                throw new UserCaretakerException(CARETAKER_ALREADY_PRESENT);
            } else {
                logger.info(RESPONSE_SAVED);
                userCaretakerRepository.save(userCaretaker);
                logger.info(EXITING_METHOD_EXECUTION);
                return new CaretakerResponse(SUCCESS, "Request sent successfully", userCaretaker);

            }
    }

    @Override
    public CaretakerResponse updateCaretakerStatus(String caretakerId) throws UserCaretakerException {
        logger.info(STARTING_METHOD_EXECUTION);
            Optional<UserCaretaker> userCaretaker = userCaretakerRepository.findById(caretakerId);
            if (userCaretaker.isPresent()) {
                userCaretaker.get().setRequestStatus(true);
                userCaretakerRepository.save(userCaretaker.get());
                logger.info("Caretaker not found with cId : {}",caretakerId);
            }else {
                throw new UserCaretakerException(NO_RECORD_FOUND);
            }
        logger.info(RESPONSE_SAVED);
        logger.info(EXITING_METHOD_EXECUTION);
        return new CaretakerResponse(SUCCESS, "Status updated", userCaretaker.get());
    }

    @Override
    public CaretakerResponsePage getPatientsUnderMe(String userId, int pageNo, int pageSize) throws UserCaretakerException, ResourceNotFoundException {

        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Fetching patients for a caretaker with id : {}",userId);
            Pageable pageable= PageRequest.of(pageNo,pageSize);
            Page<UserCaretaker> userCaretakerPage = userCaretakerRepository.getPatientsUnderMe(userId, pageable);
            if (userCaretakerPage.isEmpty()) {
                logger.debug("No patients found for userID : {}",userId);
                throw new ResourceNotFoundException(PATIENTS_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return new CaretakerResponsePage(SUCCESS,DATA_FOUND,userCaretakerPage.getTotalElements(),userCaretakerPage.getTotalPages(),pageNo,userCaretakerPage.getContent());
    }

    @Override
    public CaretakerListResponse getPatientRequests(String userId) throws UserCaretakerException, ResourceNotFoundException {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Fetching list of patient requests send to a caretaker with id : {}",userId);
            List<UserCaretaker> userCaretaker =userCaretakerRepository.getPatientRequests(userId);
            if (userCaretaker.isEmpty()) {
                logger.debug("No patient request found for id : {}", userId);
                throw new ResourceNotFoundException(PATIENT_REQUEST_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return new CaretakerListResponse(SUCCESS, DATA_FOUND, userCaretaker);
    }

    @Override
    public CaretakerListResponse getMyCaretakers(String userId) throws UserCaretakerException, ResourceNotFoundException {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Fetching list of caretakers for id : {}", userId);
            List<UserCaretaker> userCaretaker = userCaretakerRepository.getMyCaretakers(userId);
            if (userCaretaker.isEmpty()) {
                logger.debug("No Caretakers found for id : {}", userId);
                throw new ResourceNotFoundException(CARETAKERS_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return new CaretakerListResponse(SUCCESS, DATA_FOUND, userCaretaker);
    }

    @Override
    public List<UserCaretaker> getCaretakerRequestStatus(String userId) throws UserCaretakerException {

        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Updating Caretaker status for id : {}",userId);
        List<UserCaretaker> userCaretakerList= userCaretakerRepository.getCaretakerRequestStatus(userId);
            if(userCaretakerList.isEmpty()){
                logger.debug("Caretaker request not found for id :{}",userId);
                throw new UserCaretakerException(REQUEST_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return userCaretakerList;
    }


    @Override
    public CaretakerListResponse getCaretakerRequests(String userId) throws UserCaretakerException {

        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Fetching caretaker request send to a patient for id : {}", userId);
        List<UserCaretaker> userCaretaker = userCaretakerRepository.getCaretakerRequests(userId);
            if (userCaretaker.isEmpty()) {
                throw new UserCaretakerException(REQUEST_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return new CaretakerListResponse(SUCCESS, DATA_FOUND, userCaretaker);
    }

    @Override
    public CaretakerDelete deletePatientRequest(String caretakerId) throws UserCaretakerException {

        logger.info(STARTING_METHOD_EXECUTION);
        logger.info("Deleting patient request for a caretaker with id : {}",caretakerId);
        Optional<UserCaretaker> userCaretaker = userCaretakerRepository.findById(caretakerId);
            if (userCaretaker.isPresent()) {
                userCaretaker.get().setDelete(true);
                userCaretakerRepository.save(userCaretaker.get());
                logger.info(EXITING_METHOD_EXECUTION);
                return new CaretakerDelete(SUCCESS, "Deleted successfully");
            }
            logger.debug("Request not found for id : {}",caretakerId);
            throw new UserCaretakerException(REQUEST_NOT_FOUND);
    }

    @Override
    public ImageResponse sendImageToCaretaker(MultipartFile multipartFile, String filename,  String medName,String caretakerid, Integer medId) {
        try {
            logger.info(STARTING_METHOD_EXECUTION);
            Path path = Paths.get(System.getProperty("user.dir") + "/src/main/upload/static/images", filename.concat(".").concat("jpg"));
            Files.write(path, multipartFile.getBytes());

            UserMedicines userMedicines = userMedicineRepository.getMedById(medId);
            if(userMedicines.getUser()==null){
                throw new ResourceNotFoundException(NO_RECORD_FOUND);
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
        logger.info("Mapping DTO to Entity");
        return mapper.map(userCaretakerDTO, UserCaretaker.class);

    }

}