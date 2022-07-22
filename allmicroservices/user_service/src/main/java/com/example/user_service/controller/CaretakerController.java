package com.example.user_service.controller;

import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.exception.UserCaretakerException;

import com.example.user_service.exception.UserExceptionMessage;
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
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiOperation(value = "Sending request to a caretaker")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request sent successfully"),
            @ApiResponse(code = 500,message = CARETAKER_ALREADY_PRESENT),
            @ApiResponse(code = 401, message = UNAUTHORIZED)})
    @PostMapping(value = "/request", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(timeout = 12)
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    public ResponseEntity<CaretakerResponse> saveCaretaker(@Valid @RequestBody UserCaretakerDTO userCaretakerDTO) throws UserCaretakerException {
        return new ResponseEntity<>(careTakerService.saveCareTaker(userCaretakerDTO), HttpStatus.CREATED);

    }

    /**
     * Update request status if request is accepted or rejected
     */
    @ApiOperation(value = "Update request status if request is accepted or rejected")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Status updated successfully"),
            @ApiResponse(code = 500,message = NO_RECORD_FOUND),
            @ApiResponse(code = 401, message = UNAUTHORIZED)})
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @PutMapping(value = "/accept",produces = MediaType.APPLICATION_JSON_VALUE, consumes = "application/json")
     public ResponseEntity<CaretakerResponse> updateCaretakerStatus(@NotNull @NotBlank @RequestParam(name = "caretakerId") String caretakerId)
            throws UserCaretakerException {
        return new ResponseEntity<>(careTakerService.updateCaretakerStatus(caretakerId), HttpStatus.OK);

    }


    /**
     * Fetch all the patients of a particular caretaker
     */
    @ApiOperation(value = "Fetch all the patients of a particular caretaker")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = SUCCESS),
            @ApiResponse(code = 500,message = PATIENTS_NOT_FOUND),
            @ApiResponse(code = 401, message = UNAUTHORIZED)})
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/patients",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaretakerResponsePage> getPatientsUnderMe(@NotNull @NotBlank @RequestParam(name = "caretakerId") String caretakerId,
                                                                    @RequestParam(defaultValue = "0") int pageNo,
                                                                    @RequestParam(defaultValue = "3") int pageSize) throws UserCaretakerException, ResourceNotFoundException {
        return new ResponseEntity<>(careTakerService.getPatientsUnderMe(caretakerId, pageNo,pageSize), HttpStatus.OK);
    }

    /**
     * Fetch all the request sent by patients to a caretaker
     */
    @ApiOperation(value = "Fetch all the request sent by patients to a caretaker")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = SUCCESS),
            @ApiResponse(code = 500,message = PATIENT_REQUEST_NOT_FOUND),
            @ApiResponse(code = 401, message = UNAUTHORIZED)})
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/patient/requests",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaretakerListResponse> getPatientRequests(@NotNull @NotBlank @RequestParam(name = "caretakerId") String caretakerId) throws UserCaretakerException, ResourceNotFoundException {
       return new ResponseEntity<>(careTakerService.getPatientRequests(caretakerId), HttpStatus.OK);

    }

    /**
     * Fetch all the caretakers for a patient
     */
    @ApiOperation(value = "Fetch all the caretakers for a patient")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = SUCCESS),
            @ApiResponse(code = 500,message = CARETAKERS_NOT_FOUND),
            @ApiResponse(code = 401, message = UNAUTHORIZED)})
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/caretakers",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaretakerListResponse> getMyCaretakers(@NotNull @NotBlank @RequestParam(name = "patientId") String patientId) throws UserCaretakerException, ResourceNotFoundException {
        return new ResponseEntity<>(careTakerService.getMyCaretakers(patientId), HttpStatus.OK);
    }

    /**
     * Fetch caretaker requests for a patient
     */
    @ApiOperation(value = "Fetch caretaker requests for a patient")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = SUCCESS),
            @ApiResponse(code = 500,message = REQUEST_NOT_FOUND),
            @ApiResponse(code = 401, message = UNAUTHORIZED)})
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @GetMapping(value = "/caretaker/requests",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaretakerListResponse> getCaretakerRequests(@NotNull @NotBlank @RequestParam(name = "patientId") String patientId) throws UserCaretakerException {
        return new ResponseEntity<>(careTakerService.getCaretakerRequests(patientId), HttpStatus.OK);

    }

    /**
     * Deletes Patients Request for a caretaker
     */
    @ApiOperation(value = "Deletes Patients Request for a caretaker")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = SUCCESS),
            @ApiResponse(code = 500,message = REQUEST_NOT_FOUND),
            @ApiResponse(code = 401, message = UNAUTHORIZED)})
    @GetMapping(value = "/delete",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaretakerDelete> delPatientReq(@NotNull @NotBlank @RequestParam(name = "caretakerId") String caretakerId) throws UserExceptionMessage, UserCaretakerException {
        return new ResponseEntity<>(careTakerService.deletePatientRequest(caretakerId), HttpStatus.OK);
    }


    /**
     * Caretaker notifies user to take medicines
     */
    @ApiOperation(value = "Caretaker notifies user to take medicines")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = SUCCESS)})
    @GetMapping(value = "/notify/user",produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Retryable(maxAttempts = 4)// retrying up to 4 times
    @Transactional(timeout = 6)
    public ResponseEntity<String> notifyUserForMed(@NotNull @NotBlank @RequestParam(name = "fcmToken") String fcmToken,@NotNull @NotBlank @RequestParam(name = "medicineName") String medName) {

        rabbitTemplate.convertAndSend(topicExchange,routingKeyNotify, new NotificationMessage(fcmToken, "Take medicine", "patient", medName, ""));
        return new ResponseEntity<>("Ok", HttpStatus.OK);

    }

    /**
     * Sends image to caretaker for strict adherence
     * @param sendImageDto used as request body for details related to image.
     */
    @ApiOperation(value = "Sends image to caretaker for strict adherence")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = SUCCESS),
            @ApiResponse(code = 500,message = NO_RECORD_FOUND),
            @ApiResponse(code = 401, message = UNAUTHORIZED)})
    @PostMapping(value = "/image",produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(timeout = 10)
    public ResponseEntity<ImageResponse> sendImageToCaretaker(@Valid @ModelAttribute("sendImageDto") SendImageDto sendImageDto) throws IOException, UserCaretakerException {

        logger.info("sending image");
        return new ResponseEntity<>(careTakerService.sendImageToCaretaker(sendImageDto.getImage(),sendImageDto.getName(),sendImageDto.getMedicineName(),sendImageDto.getId(),sendImageDto.getMedicineId()),HttpStatus.CREATED);
    }


}