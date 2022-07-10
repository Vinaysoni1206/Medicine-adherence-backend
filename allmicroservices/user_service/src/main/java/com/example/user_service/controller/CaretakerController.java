package com.example.user_service.controller;

import com.example.user_service.exception.UserCaretakerException;

import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.user.UserCaretaker;
import com.example.user_service.pojos.Notificationmessage;
import com.example.user_service.pojos.dto.SendImageDto;
import com.example.user_service.pojos.dto.UserCaretakerDTO;
import com.example.user_service.pojos.response.caretaker.CaretakerDelete;
import com.example.user_service.pojos.response.caretaker.CaretakerResponse;
import com.example.user_service.pojos.response.ImageResponse;
import com.example.user_service.pojos.response.caretaker.CaretakerResponsePage;
import com.example.user_service.service.caretaker.CareTakerService;
import com.example.user_service.util.Messages;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1")
@Validated
public class CaretakerController {
    private final CareTakerService careTakerService;
    RabbitTemplate rabbitTemplate;

    @Value("${project.rabbitmq.exchange}")
    private String topicExchange;

    @Value("${project.rabbitmq.routingkey2}")
    private String routingKey2;

    Logger logger = LoggerFactory.getLogger(CaretakerController.class);
    CaretakerController(CareTakerService careTakerService, RabbitTemplate rabbitTemplate){
        this.careTakerService= careTakerService;
        this.rabbitTemplate = rabbitTemplate;
    }

    // save caretaker for a patients
    @PostMapping(value = "/request", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(timeout = 12)
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    public ResponseEntity<CaretakerResponse> saveCaretaker(@Valid @RequestBody UserCaretakerDTO userCaretakerDTO) throws UserCaretakerException {
        UserCaretaker userCaretaker = careTakerService.saveCareTaker(userCaretakerDTO);
        CaretakerResponse caretakerResponse = new CaretakerResponse(Messages.SUCCESS, "Request sent successfully", userCaretaker);
        return new ResponseEntity<>(caretakerResponse, HttpStatus.OK);

    }

    // update request status if request is accepted or rejected
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @PutMapping(value = "/accept",produces = MediaType.APPLICATION_JSON_VALUE, consumes = "application/json")
    public ResponseEntity<CaretakerResponse> updateCaretakerStatus(@NotNull @NotBlank @RequestParam(name = "cId") String cId)
            throws UserCaretakerException {
        UserCaretaker userCaretaker = careTakerService.updateCaretakerStatus(cId);
        CaretakerResponse caretakerResponse = new CaretakerResponse(Messages.SUCCESS, "Status updated", userCaretaker);
        return new ResponseEntity<>(caretakerResponse, HttpStatus.OK);

    }


    // fetch all the patients of a particular caretaker
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/patients",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaretakerResponsePage> getPatientsUnderMe(@NotNull @NotBlank @RequestParam(name = "caretakerId") String userId,
                                                                    @RequestParam(defaultValue = "0") int pageNo,
                                                                    @RequestParam(defaultValue = "3") int pageSize) throws UserCaretakerException {

        return new ResponseEntity<>(careTakerService.getPatientsUnderMe(userId, pageNo,pageSize), HttpStatus.OK);
    }

    // fetch all the request sent by a patients to a caretaker
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/patient/requests",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaretakerDelete.CaretakerListResponse> getPatientRequestsC(@NotNull @NotBlank @RequestParam(name = "caretakerId") String userId) throws UserCaretakerException {
        List<UserCaretaker> userCaretakerList = careTakerService.getPatientRequests(userId);
        CaretakerDelete.CaretakerListResponse caretakerResponse1 = new CaretakerDelete.CaretakerListResponse(Messages.SUCCESS, Messages.DATA_FOUND, userCaretakerList);
        return new ResponseEntity<>(caretakerResponse1, HttpStatus.OK);

    }

    // where the patients can view all his caretakers
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/caretakers",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaretakerDelete.CaretakerListResponse> getMyCaretakers(@NotNull @NotBlank @RequestParam(name = "patientId") String userId) throws UserCaretakerException {
        List<UserCaretaker> userCaretakerList = careTakerService.getMyCaretakers(userId);
        CaretakerDelete.CaretakerListResponse caretakerResponse1 = new CaretakerDelete.CaretakerListResponse(Messages.SUCCESS, Messages.DATA_FOUND, userCaretakerList);
        return new ResponseEntity<>(caretakerResponse1, HttpStatus.OK);
    }

    // to check the status of a request by caretaker

    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/caretaker/requests",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaretakerDelete.CaretakerListResponse> getCaretakerRequestsP(@NotNull @NotBlank @RequestParam(name = "patientId") String userId) throws UserCaretakerException {
        List<UserCaretaker> userCaretakerList = careTakerService.getCaretakerRequestsP(userId);
        CaretakerDelete.CaretakerListResponse caretakerResponse1 = new CaretakerDelete.CaretakerListResponse(Messages.SUCCESS, Messages.DATA_FOUND, userCaretakerList);
        return new ResponseEntity<>(caretakerResponse1, HttpStatus.OK);

    }

    @GetMapping(value = "/delete",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaretakerDelete> delPatientReq(@NotNull @NotBlank @RequestParam(name = "cId") String cId) throws UserExceptionMessage, UserCaretakerException {
        String delPatientStatus = careTakerService.delPatientReq(cId);
        CaretakerDelete caretakerDelete = new CaretakerDelete(delPatientStatus, "Deleted successfully");
        return new ResponseEntity<>(caretakerDelete, HttpStatus.OK);
    }

    @GetMapping(value = "/notifyuser",produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @Transactional(timeout = 6)
    public ResponseEntity<String> notifyUserForMed(@NotNull @NotBlank @RequestParam(name = "fcmToken") String fcmToken,@NotNull @NotBlank @RequestParam(name = "medname") String body) {

        rabbitTemplate.convertAndSend(topicExchange,routingKey2, new Notificationmessage(fcmToken, "Take medicine", "patient", body, ""));
        return new ResponseEntity<>("Ok", HttpStatus.OK);

    }

    @PostMapping(value = "/image",produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(timeout = 10)
    public ResponseEntity<ImageResponse> sendImageToCaretaker(@Valid @ModelAttribute("sendImageDto") SendImageDto sendImageDto) throws IOException, UserCaretakerException {

        logger.info("sending image");
        ImageResponse imageResponse= careTakerService.sendImageToCaretaker(sendImageDto.getImage(),sendImageDto.getName(),sendImageDto.getMedName(),sendImageDto.getId(),sendImageDto.getMedId());
        return new ResponseEntity<>(imageResponse,HttpStatus.OK);
    }

////
}