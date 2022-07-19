package com.example.user_service.exception;

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
        ResponseEntity<UserResponse> userResponseReal = userExceptions.getuserException(new UserExceptionMessage(Constants.DATA_NOT_FOUND));
        Assertions.assertEquals(userResponseExpected.getMessage(),userResponseReal.getBody().getMessage());
        Assertions.assertEquals(userResponseExpected.getStatus(),userResponseReal.getBody().getStatus());
        Assertions.assertEquals(null,userResponseReal.getBody().getUser());
        Assertions.assertEquals(HttpStatus.NOT_FOUND,userResponseReal.getStatusCode());
        }

    @Test
    @DisplayName("Test for User Caretaker Exception")
    void getcaretakerexception() {
        CaretakerResponse caretakerResponseExpected = new CaretakerResponse(Constants.ERROR, Constants.DATA_NOT_FOUND, null);
        ResponseEntity<CaretakerResponse> caretakerResponseReal = userExceptions.getcaretakerexception(new UserCaretakerException(Constants.DATA_NOT_FOUND));
        Assertions.assertEquals(caretakerResponseExpected.getMessage(),caretakerResponseReal.getBody().getMessage());
        Assertions.assertEquals(HttpStatus.NOT_FOUND,caretakerResponseReal.getStatusCode());
    }

    @Test
    void getCaretakerNotvalidExceptionConstraint() {
        CaretakerResponse caretakerResponseExpected = new CaretakerResponse(Constants.ERROR, Constants.DATA_NOT_FOUND, null);
        ResponseEntity<CaretakerResponse> caretakerResponseReal = userExceptions.getCaretakerNotvalidExceptionConstraint(new ConstraintViolationException(Constants.DATA_NOT_FOUND,null));
        Assertions.assertEquals(caretakerResponseExpected.getMessage(),caretakerResponseReal.getBody().getMessage());
    }

    @Test
    void getCaretakerNotvalidExceptionValidation() {
        CaretakerResponse caretakerResponseExpected = new CaretakerResponse(Constants.ERROR, Constants.DATA_NOT_FOUND, null);
        ResponseEntity<CaretakerResponse> caretakerResponseReal = userExceptions.getCaretakerNotvalidExceptionValidation(new ValidationException(Constants.DATA_NOT_FOUND));
        Assertions.assertEquals(caretakerResponseExpected.getMessage(),caretakerResponseReal.getBody().getMessage());
        Assertions.assertEquals(HttpStatus.NOT_FOUND,caretakerResponseReal.getStatusCode());
    }

    @Test
    @DisplayName("Test for User Medicine Exception")
    void getUserMedicineException() {
        MedicineResponse medicineResponseExpected = new MedicineResponse(Constants.ERROR, Constants.DATA_NOT_FOUND, null);
        ResponseEntity<MedicineResponse> medicineResponseReal= userExceptions.getUserMedicineException(new UserMedicineException(Constants.DATA_NOT_FOUND));
        Assertions.assertEquals(medicineResponseExpected.getMessage(),medicineResponseReal.getBody().getMessage());
        Assertions.assertEquals(HttpStatus.NOT_FOUND,medicineResponseReal.getStatusCode());
    }
}