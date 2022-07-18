package com.example.user_service.service.caretaker;


import com.example.user_service.exception.UserCaretakerException;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.image.Image;
import com.example.user_service.model.user.UserCaretaker;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.pojos.Notificationmessage;
import com.example.user_service.pojos.dto.UserCaretakerDTO;
import com.example.user_service.pojos.response.image.ImageResponse;
import com.example.user_service.pojos.response.caretaker.CaretakerResponsePage;
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

import static com.example.user_service.util.Messages.*;
import static com.example.user_service.util.Messages.LoggerConstants.*;

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

        logger.info(STARTING_METHOD_EXECUTION);
            UserCaretaker userCaretaker = mapToEntity(userCaretakerDTO);
            userCaretaker.setCreatedAt(Datehelper.getcurrentdatatime());
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
    public UserCaretaker updateCaretakerStatus(String cId) throws UserCaretakerException {
        logger.info(STARTING_METHOD_EXECUTION);
            UserCaretaker uc = userCaretakerRepository.getById(cId);
            if (uc.getCaretakerId()==null) {
                logger.info("Caretaker not found with cId : {}",cId);
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
    public List<UserCaretaker> getCaretakerRequestsP(String userId) throws UserCaretakerException {

        logger.info(STARTING_METHOD_EXECUTION);
        List<UserCaretaker> userCaretaker = userCaretakerRepository.getCaretakerRequestsP(userId);
            if (userCaretaker.isEmpty()) {
                throw new UserCaretakerException(DATA_NOT_FOUND);
            }
        logger.info(EXITING_METHOD_EXECUTION);
        return userCaretaker;
    }

    @Override
    public String delPatientReq(String cId) throws UserExceptionMessage, UserCaretakerException {

        logger.info(STARTING_METHOD_EXECUTION);
        Optional<UserCaretaker> userCaretaker = userCaretakerRepository.findById(cId);
            if (userCaretaker.isPresent()) {
                userCaretakerRepository.delete(userCaretaker.get());
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
            if(userMedicines.getUserEntity()==null){
                throw new UserCaretakerException(DATA_NOT_FOUND);
            }
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