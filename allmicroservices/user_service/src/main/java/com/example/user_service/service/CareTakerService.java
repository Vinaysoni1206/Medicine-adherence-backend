package com.example.user_service.service;

import com.example.user_service.exception.UserCaretakerException;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.UserCaretaker;
import com.example.user_service.pojos.request.UserCaretakerDTO;
import com.example.user_service.pojos.response.ImageResponse;
import com.example.user_service.pojos.response.CaretakerResponsePage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * This is an interface for caretaker service
 */
public interface CareTakerService {

     UserCaretaker saveCareTaker(UserCaretakerDTO userCaretakerDTO) throws UserCaretakerException;

     UserCaretaker updateCaretakerStatus(String caretakerId) throws UserCaretakerException;

     CaretakerResponsePage getPatientsUnderMe(String userId, int pageNo, int pageSize)throws UserCaretakerException;

     List<UserCaretaker> getPatientRequests(String userId) throws UserCaretakerException;

     List<UserCaretaker> getMyCaretakers(String userId) throws UserCaretakerException;

     List<UserCaretaker> getCaretakerRequestStatus(String userId) throws UserCaretakerException;

     List<UserCaretaker> getCaretakerRequests(String userId) throws UserCaretakerException;

     String delPatientReq(String caretakerId) throws UserExceptionMessage, UserCaretakerException;

     ImageResponse sendImageToCaretaker(MultipartFile multipartFile , String filename , String medName,String caretakerId ,  Integer medId) throws IOException , UserCaretakerException;
}
