package com.example.user_service.exception;

import com.example.user_service.pojos.response.CaretakerResponse;
import com.example.user_service.pojos.response.MedicineResponse;
import com.example.user_service.pojos.response.UserResponse;
import com.example.user_service.util.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;


/**
 * This class is used to send error response
 */
@ControllerAdvice
public class GlobalExceptions {

    /**
     * Returns user exception response
     */
    @ExceptionHandler({UserExceptionMessage.class})
    public ResponseEntity<UserResponse> getuserException(final UserExceptionMessage uem) {
        UserResponse userResponse = new UserResponse(Constants.ERROR, uem.getMessage(), null, "", "");
        return new ResponseEntity<>(userResponse, HttpStatus.NOT_FOUND);

    }

    /**
     * Returns caretaker exception response
     */
    @ExceptionHandler({UserCaretakerException.class})
    public ResponseEntity<CaretakerResponse> getcaretakerexception(final UserCaretakerException uce) {
        CaretakerResponse caretakerResponse = new CaretakerResponse(Constants.ERROR, uce.getMessage(), null);
        return new ResponseEntity<>(caretakerResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<CaretakerResponse> getCaretakerNotvalidExceptionConstraint(ConstraintViolationException uce) {
        CaretakerResponse caretakerResponse = new CaretakerResponse(Constants.ERROR, uce.getMessage(), null);
        return new ResponseEntity<>(caretakerResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<CaretakerResponse> getCaretakerNotvalidExceptionValidation(ValidationException uce) {
        CaretakerResponse caretakerResponse = new CaretakerResponse(Constants.ERROR, uce.getMessage(), null);
        return new ResponseEntity<>(caretakerResponse, HttpStatus.NOT_FOUND);

    }
    /**
     * Returns user medicine exception response
     */
    @ExceptionHandler({UserMedicineException.class})
    public ResponseEntity<MedicineResponse> getUserMedicineException(UserMedicineException udm) {

        MedicineResponse medicineResponse = new MedicineResponse(Constants.ERROR, udm.getMessage(), null);
        return new ResponseEntity<>(medicineResponse, HttpStatus.NOT_FOUND);

    }




}
