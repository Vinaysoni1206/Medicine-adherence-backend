package com.example.user_service.exception;

import com.example.user_service.pojos.response.CustomExceptionResponse;
import com.example.user_service.pojos.response.MedicineResponse;
import com.example.user_service.pojos.response.CaretakerResponse;
import com.example.user_service.pojos.response.UserResponse;
import com.example.user_service.util.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;


@SpringBootTest
class UserExceptionsTest {

    @InjectMocks
    GlobalExceptions userExceptions;

    @Mock
    UserCaretakerException userCaretakerException;

    @Mock
    UserMedicineException userMedicineException;

    @Mock
    UserExceptionMessage userExceptionMessage;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test for User Exception")
    void getuserException() {
        UserResponse userResponseExpected= new UserResponse(Constants.ERROR, Constants.DATA_NOT_FOUND,null,"","");
        ResponseEntity<UserResponse> userResponseReal = userExceptions.getUserException(new UserExceptionMessage(Constants.DATA_NOT_FOUND));
        Assertions.assertEquals(userResponseExpected.getMessage(),userResponseReal.getBody().getMessage());
        Assertions.assertEquals(userResponseExpected.getStatus(),userResponseReal.getBody().getStatus());
        Assertions.assertEquals(null,userResponseReal.getBody().getUser());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,userResponseReal.getStatusCode());
        }

    @Test
    @DisplayName("Test for User Caretaker Exception")
    void getCaretakerException() {
        CaretakerResponse caretakerResponseExpected = new CaretakerResponse(Constants.ERROR, Constants.DATA_NOT_FOUND, null);
        ResponseEntity<CaretakerResponse> caretakerResponseReal = userExceptions.getCaretakerException(new UserCaretakerException(Constants.DATA_NOT_FOUND));
        Assertions.assertEquals(caretakerResponseExpected.getMessage(),caretakerResponseReal.getBody().getMessage());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,caretakerResponseReal.getStatusCode());
    }

    @Test
    void getCaretakerNotValidExceptionConstraint() {
        CaretakerResponse caretakerResponseExpected = new CaretakerResponse(Constants.ERROR, Constants.DATA_NOT_FOUND, null);
        ResponseEntity<CaretakerResponse> caretakerResponseReal = userExceptions.getCaretakerNotValidExceptionConstraint(new ConstraintViolationException(Constants.DATA_NOT_FOUND,null));
        Assertions.assertEquals(caretakerResponseExpected.getMessage(),caretakerResponseReal.getBody().getMessage());
    }

    @Test
    void getCaretakerNotValidExceptionValidation() {
        CaretakerResponse caretakerResponseExpected = new CaretakerResponse(Constants.ERROR, Constants.DATA_NOT_FOUND, null);
        ResponseEntity<CaretakerResponse> caretakerResponseReal = userExceptions.getCaretakerNotValidExceptionValidation(new ValidationException(Constants.DATA_NOT_FOUND));
        Assertions.assertEquals(caretakerResponseExpected.getMessage(),caretakerResponseReal.getBody().getMessage());
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY,caretakerResponseReal.getStatusCode());
    }

    @Test
    @DisplayName("Test for User Medicine Exception")
    void getUserMedicineException() {
        MedicineResponse medicineResponseExpected = new MedicineResponse(Constants.ERROR, Constants.DATA_NOT_FOUND, null);
        ResponseEntity<MedicineResponse> medicineResponseReal= userExceptions.getUserMedicineException(new UserMedicineException(Constants.DATA_NOT_FOUND));
        Assertions.assertEquals(medicineResponseExpected.getMessage(),medicineResponseReal.getBody().getMessage());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,medicineResponseReal.getStatusCode());
    }

    @Test
    @DisplayName("Test for Resource not found exception")
    void handleResourceNotFoundTest(){
        CustomExceptionResponse customExceptionResponse= new CustomExceptionResponse();
        CustomExceptionResponse customExceptionResponseReal= userExceptions.handleResourceNotFound(new ResourceNotFoundException("NOT_FOUND"));
        Assertions.assertEquals("NOT_FOUND",customExceptionResponseReal.getMessage());
    }
}