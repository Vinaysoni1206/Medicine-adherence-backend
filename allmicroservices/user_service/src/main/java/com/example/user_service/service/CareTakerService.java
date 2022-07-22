package com.example.user_service.service;

import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.exception.UserCaretakerException;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.UserCaretaker;
import com.example.user_service.pojos.request.UserCaretakerDTO;
import com.example.user_service.pojos.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * This is an interface for caretaker service
 */
public interface CareTakerService {

     CaretakerResponse saveCareTaker(UserCaretakerDTO userCaretakerDTO) throws UserCaretakerException;

     CaretakerResponse updateCaretakerStatus(String caretakerId) throws UserCaretakerException;

     CaretakerResponsePage getPatientsUnderMe(String userId, int pageNo, int pageSize) throws UserCaretakerException, ResourceNotFoundException;

     CaretakerListResponse getPatientRequests(String userId) throws UserCaretakerException, ResourceNotFoundException;

     CaretakerListResponse getMyCaretakers(String userId) throws UserCaretakerException, ResourceNotFoundException;

     List<UserCaretaker> getCaretakerRequestStatus(String userId) throws UserCaretakerException;

     CaretakerListResponse getCaretakerRequests(String userId) throws UserCaretakerException;

     CaretakerDelete deletePatientRequest(String caretakerId) throws UserExceptionMessage, UserCaretakerException;

     ImageResponse sendImageToCaretaker(MultipartFile multipartFile , String filename , String medicineName,String caretakerId ,  Integer medicineId) throws IOException , UserCaretakerException;
}
