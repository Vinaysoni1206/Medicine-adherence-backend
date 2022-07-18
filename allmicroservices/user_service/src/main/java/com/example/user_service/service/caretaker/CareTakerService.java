package com.example.user_service.service.caretaker;

import com.example.user_service.exception.caretaker.UserCaretakerException;
import com.example.user_service.exception.user.UserExceptionMessage;
import com.example.user_service.model.user.UserCaretaker;
import com.example.user_service.pojos.dto.caretaker.UserCaretakerDTO;
import com.example.user_service.pojos.response.image.ImageResponse;
import com.example.user_service.pojos.response.caretaker.CaretakerResponsePage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CareTakerService {

     UserCaretaker saveCareTaker(UserCaretakerDTO userCaretakerDTO) throws UserCaretakerException;

     UserCaretaker updateCaretakerStatus(String cId) throws UserCaretakerException;

     CaretakerResponsePage getPatientsUnderMe(String userId, int pageNo, int pageSize)throws UserCaretakerException;

     List<UserCaretaker> getPatientRequests(String userId) throws UserCaretakerException;

     List<UserCaretaker> getMyCaretakers(String userId) throws UserCaretakerException;

     List<UserCaretaker> getCaretakerRequestStatus(String userId) throws UserCaretakerException;

     List<UserCaretaker> getCaretakerRequestsP(String userId) throws UserCaretakerException;

     String delPatientReq(String cId) throws UserExceptionMessage, UserCaretakerException;

     ImageResponse sendImageToCaretaker(MultipartFile multipartFile , String filename , String medName,String caretakerId ,  Integer medId) throws IOException , UserCaretakerException;
}
