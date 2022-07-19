package com.example.user_service.controller;

import com.example.user_service.exception.UserCaretakerException;

import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.UserCaretaker;
import com.example.user_service.pojos.response.NotificationMessage;
import com.example.user_service.pojos.request.SendImageDto;
import com.example.user_service.pojos.request.UserCaretakerDTO;
import com.example.user_service.pojos.response.CaretakerDelete;
import com.example.user_service.pojos.response.CaretakerListResponse;
import com.example.user_service.pojos.response.CaretakerResponse;
import com.example.user_service.pojos.response.ImageResponse;
import com.example.user_service.pojos.response.CaretakerResponsePage;
import com.example.user_service.service.CareTakerService;
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

import static com.example.user_service.util.Constants.*;

/**
 * This controller is used to create restful web services for caretaker
 */
@RestController
@RequestMapping(path = "/api/v1")
@Validated
public class CaretakerController {
    private final CareTakerService careTakerService;
    RabbitTemplate rabbitTemplate;

    @Value("${project.rabbitmq.exchange}")
    private String topicExchange;

    @Value("${project.rabbitmq.routingKeyNotify}")
    private String routingKeyNotify;

    Logger logger = LoggerFactory.getLogger(CaretakerController.class);
    CaretakerController(CareTakerService careTakerService, RabbitTemplate rabbitTemplate){
        this.careTakerService= careTakerService;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Saves caretaker for patients
     * @param userCaretakerDTO is used as request body for saving a caretaker
     */
    @PostMapping(value = "/request", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(timeout = 12)
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    public ResponseEntity<CaretakerResponse> saveCaretaker(@Valid @RequestBody UserCaretakerDTO userCaretakerDTO) throws UserCaretakerException {
        UserCaretaker userCaretaker = careTakerService.saveCareTaker(userCaretakerDTO);
        CaretakerResponse caretakerResponse = new CaretakerResponse(SUCCESS, "Request sent successfully", userCaretaker);
        return new ResponseEntity<>(caretakerResponse, HttpStatus.CREATED);

    }

    /**
     * Update request status if request is accepted or rejected
     */
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @PutMapping(value = "/accept",produces = MediaType.APPLICATION_JSON_VALUE, consumes = "application/json")
    public ResponseEntity<CaretakerResponse> updateCaretakerStatus(@NotNull @NotBlank @RequestParam(name = "caretakerId") String caretakerId)
            throws UserCaretakerException {
        UserCaretaker userCaretaker = careTakerService.updateCaretakerStatus(caretakerId);
        CaretakerResponse caretakerResponse = new CaretakerResponse(SUCCESS, "Status updated", userCaretaker);
        return new ResponseEntity<>(caretakerResponse, HttpStatus.OK);

    }


    /**
     * Fetch all the patients of a particular caretaker
     */
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/patients",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaretakerResponsePage> getPatientsUnderMe(@NotNull @NotBlank @RequestParam(name = "caretakerId") String caretakerId,
                                                                    @RequestParam(defaultValue = "0") int pageNo,
                                                                    @RequestParam(defaultValue = "3") int pageSize) throws UserCaretakerException {

        return new ResponseEntity<>(careTakerService.getPatientsUnderMe(caretakerId, pageNo,pageSize), HttpStatus.OK);
    }

    /**
     * Fetch all the request sent by patients to a caretaker
     */
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/patient/requests",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaretakerListResponse> getPatientRequests(@NotNull @NotBlank @RequestParam(name = "caretakerId") String caretakerId) throws UserCaretakerException {
        List<UserCaretaker> userCaretakerList = careTakerService.getPatientRequests(caretakerId);
        CaretakerListResponse caretakerResponseList = new CaretakerListResponse(SUCCESS, DATA_FOUND, userCaretakerList);
        return new ResponseEntity<>(caretakerResponseList, HttpStatus.OK);

    }

    /**
     * Fetch all the caretakers for a patient
     */
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/caretakers",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaretakerListResponse> getMyCaretakers(@NotNull @NotBlank @RequestParam(name = "patientId") String patientId) throws UserCaretakerException {
        List<UserCaretaker> userCaretakerList = careTakerService.getMyCaretakers(patientId);
        CaretakerListResponse caretakerResponseList = new CaretakerListResponse(SUCCESS, DATA_FOUND, userCaretakerList);
        return new ResponseEntity<>(caretakerResponseList, HttpStatus.OK);
    }

    /**
     * Fetch caretaker requests for a patient
     */
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/caretaker/requests",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaretakerListResponse> getCaretakerRequests(@NotNull @NotBlank @RequestParam(name = "patientId") String patientId) throws UserCaretakerException {
        List<UserCaretaker> userCaretakerList = careTakerService.getCaretakerRequests(patientId);
        CaretakerListResponse caretakerResponseList = new CaretakerListResponse(SUCCESS, DATA_FOUND, userCaretakerList);
        return new ResponseEntity<>(caretakerResponseList, HttpStatus.OK);

    }

    /**
     * Deletes Patients Request for a caretaker
     */
    @GetMapping(value = "/delete",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaretakerDelete> delPatientReq(@NotNull @NotBlank @RequestParam(name = "caretakerId") String caretakerId) throws UserExceptionMessage, UserCaretakerException {
        String delPatientStatus = careTakerService.delPatientReq(caretakerId);
        CaretakerDelete caretakerDelete = new CaretakerDelete(delPatientStatus, "Deleted successfully");
        return new ResponseEntity<>(caretakerDelete, HttpStatus.OK);
    }


    /**
     * Caretaker notifies user to take medicines
     */
    @GetMapping(value = "/notify/user",produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @Transactional(timeout = 6)
    public ResponseEntity<String> notifyUserForMed(@NotNull @NotBlank @RequestParam(name = "fcmToken") String fcmToken,@NotNull @NotBlank @RequestParam(name = "medName") String medName) {

        rabbitTemplate.convertAndSend(topicExchange,routingKeyNotify, new NotificationMessage(fcmToken, "Take medicine", "patient", medName, ""));
        return new ResponseEntity<>("Ok", HttpStatus.OK);

    }

    /**
     * Sends image to caretaker for strict adherence
     * @param sendImageDto used as request body for details related to image.
     */
    @PostMapping(value = "/image",produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(timeout = 10)
    public ResponseEntity<ImageResponse> sendImageToCaretaker(@Valid @ModelAttribute("sendImageDto") SendImageDto sendImageDto) throws IOException, UserCaretakerException {

        logger.info("sending image");
        ImageResponse imageResponse= careTakerService.sendImageToCaretaker(sendImageDto.getImage(),sendImageDto.getName(),sendImageDto.getMedName(),sendImageDto.getId(),sendImageDto.getMedId());
        return new ResponseEntity<>(imageResponse,HttpStatus.CREATED);
    }


}