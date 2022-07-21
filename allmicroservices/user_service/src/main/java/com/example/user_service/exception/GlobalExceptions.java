package com.example.user_service.exception;

import com.example.user_service.pojos.response.CaretakerResponse;
import com.example.user_service.pojos.response.CustomExceptionResponse;
import com.example.user_service.pojos.response.MedicineResponse;
import com.example.user_service.pojos.response.UserResponse;
import com.example.user_service.util.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import static com.example.user_service.util.Constants.FAILED;


/**
 * This class is used to send error response
 */
@ControllerAdvice
public class GlobalExceptions extends ResponseEntityExceptionHandler {

    /**
     * Returns user exception response
     */
    @ExceptionHandler({UserExceptionMessage.class})
    public ResponseEntity<UserResponse> getUserException(final UserExceptionMessage uem) {
        UserResponse userResponse = new UserResponse(Constants.ERROR, uem.getMessage(), null, "", "");
        return new ResponseEntity<>(userResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    /**
     * Returns caretaker exception response
     */
    @ExceptionHandler({UserCaretakerException.class})
    public ResponseEntity<CaretakerResponse> getCaretakerException(final UserCaretakerException uce) {
        CaretakerResponse caretakerResponse = new CaretakerResponse(Constants.ERROR, uce.getMessage(), null);
        return new ResponseEntity<>(caretakerResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<CaretakerResponse> getCaretakerNotValidExceptionConstraint(ConstraintViolationException uce) {
        CaretakerResponse caretakerResponse = new CaretakerResponse(Constants.ERROR, uce.getMessage(), null);
        return new ResponseEntity<>(caretakerResponse, HttpStatus.CONFLICT);

    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<CaretakerResponse> getCaretakerNotValidExceptionValidation(ValidationException uce) {
        CaretakerResponse caretakerResponse = new CaretakerResponse(FAILED, uce.getMessage(), null);
        return new ResponseEntity<>(caretakerResponse, HttpStatus.UNPROCESSABLE_ENTITY);

    }
    /**
     * Returns user medicine exception response
     */
    @ExceptionHandler({UserMedicineException.class})
    public ResponseEntity<MedicineResponse> getUserMedicineException(UserMedicineException udm) {

        MedicineResponse medicineResponse = new MedicineResponse(FAILED, udm.getMessage(), null);
        return new ResponseEntity<>(medicineResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public @ResponseBody CustomExceptionResponse handleResourceNotFound(final ResourceNotFoundException exception) {
        return new CustomExceptionResponse(FAILED, exception.getMessage(), "NOT_FOUND");
    }

}
