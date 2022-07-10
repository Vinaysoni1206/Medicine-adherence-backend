package com.example.user_service.exception;

import com.example.user_service.pojos.response.MedicineResponse;
import com.example.user_service.pojos.response.caretaker.CaretakerResponse;
import com.example.user_service.pojos.response.user.UserResponse;
import com.example.user_service.util.Messages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.ValidationException;


@SpringBootTest
class UserExceptionsTest {

    @Autowired
    UserExceptions userExceptions;



    @Test
    void getuserException() {
        UserResponse userResponseExpected= new UserResponse(Messages.ERROR,Messages.DATA_NOT_FOUND,null,"","");
        ResponseEntity<UserResponse> userResponseReal = userExceptions.getuserException(new UserExceptionMessage(Messages.DATA_NOT_FOUND));
        Assertions.assertEquals(userResponseExpected.getMessage(),userResponseReal.getBody().getMessage());
        Assertions.assertEquals(userResponseExpected.getStatus(),userResponseReal.getBody().getStatus());
        Assertions.assertEquals(null,userResponseReal.getBody().getUserEntity());
        Assertions.assertEquals(HttpStatus.NOT_FOUND,userResponseReal.getStatusCode());
        }

    @Test
    void getcaretakerexception() {
        CaretakerResponse caretakerResponseExpected = new CaretakerResponse(Messages.ERROR, Messages.DATA_NOT_FOUND, null);
        ResponseEntity<CaretakerResponse> caretakerResponseReal = userExceptions.getcaretakerexception(new UserCaretakerException(Messages.DATA_NOT_FOUND));
        Assertions.assertEquals(caretakerResponseExpected.getMessage(),caretakerResponseReal.getBody().getMessage());
        Assertions.assertEquals(HttpStatus.NOT_FOUND,caretakerResponseReal.getStatusCode());
    }

//    @Test
//    void getCaretakerNotvalidExceptionConstraint() {
//        CaretakerResponse caretakerResponseExpected = new CaretakerResponse(Messages.ERROR, Messages.DATA_NOT_FOUND, null);
//        ResponseEntity<CaretakerResponse> caretakerResponseReal = userExceptions.getCaretakerNotvalidExceptionConstraint(new ConstraintViolationException(Messages.DATA_NOT_FOUND,));
//    }

//    @Test
//    void getCaretakerNotvalidExceptionMethod() {
//        MethodParameter parameter;
//        CaretakerResponse caretakerResponseExpected = new CaretakerResponse(Messages.ERROR, Messages.DATA_NOT_FOUND, null);
//        ResponseEntity<CaretakerResponse> caretakerResponseReal = userExceptions.getCaretakerNotvalidExceptionMethod(new MethodArgumentNotValidException(parameter,));
//        Assertions.assertEquals(caretakerResponseExpected.getMessage(),caretakerResponseReal.getBody().getMessage());
//    }


    @Test
    void getCaretakerNotvalidExceptionValidation() {
        CaretakerResponse caretakerResponseExpected = new CaretakerResponse(Messages.ERROR, Messages.DATA_NOT_FOUND, null);
        ResponseEntity<CaretakerResponse> caretakerResponseReal = userExceptions.getCaretakerNotvalidExceptionValidation(new ValidationException(Messages.DATA_NOT_FOUND));
        Assertions.assertEquals(caretakerResponseExpected.getMessage(),caretakerResponseReal.getBody().getMessage());
        Assertions.assertEquals(HttpStatus.NOT_FOUND,caretakerResponseReal.getStatusCode());
    }

    @Test
    void getUserMedicineException() {
        MedicineResponse medicineResponseExpected = new MedicineResponse(Messages.ERROR, Messages.DATA_NOT_FOUND, null);
        ResponseEntity<MedicineResponse> medicineResponseReal= userExceptions.getUserMedicineException(new UserMedicineException(Messages.DATA_NOT_FOUND));
        Assertions.assertEquals(medicineResponseExpected.getMessage(),medicineResponseReal.getBody().getMessage());
        Assertions.assertEquals(HttpStatus.NOT_FOUND,medicineResponseReal.getStatusCode());
    }
}