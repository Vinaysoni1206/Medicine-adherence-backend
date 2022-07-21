package com.example.user_service.util;

/**
 * This is class for handling all the hardcoded messages
 */
public class Constants {


    Constants(){}
    public static final String UNAUTHORIZED= "UNAUTHORIZED";
    public static final String SAVED_USER = "Saved user successfully";
    public static final String INVALID_EMAIL="Invalid- email";
    public static final String FIELD_CANNOT_BE_NULL="Field cannot be null";
    public static final String REQUEST_NOT_FOUND= "Request not found";
    public static final String PATIENT_REQUEST_NOT_FOUND= "No Patient request found";
    public static final String PATIENTS_NOT_FOUND= "No patients found";

    public static final String CARETAKERS_NOT_FOUND= "No caretakers found";
    public static final String NO_MEDICINE_PRESENT= "No medicine found";
    public static final String USER_NOT_FOUND= "User not found";
    public static final String NO_USERS_FOUND= "No users found";
    public static final String USER_ID_NOT_FOUND= "User with this ID is not present";
    public static final String USER_NAME_NOT_FOUND= "User with this name is not present";
    public static final String USER_EMAIL_NOT_FOUND= "User with this email is not present";
    public static final String USER_NOT_SAVED= "User not saved successfully";
    public static final String INVALID_REFRESH_TOKEN= "Invalid refresh token";
    public static final String UNABLE_TO_SYNC = "Unable to sync";
    public static  final String DATA_NOT_FOUND = "Data not found";
    public static final String ERROR = "Error!!";
    public  static  final String DATA_FOUND = "Data found ";

    public static final String SUCCESS = "Success";
    public static final String FAILED = "Failed";
    public static final String SENT_SUCCESS = "Image sent successfully ";
    public static final String MEDICINES_NOT_FOUND = "Medicines not found";
    public static final String USER_ALREADY_PRESENT = "User is already present";
    public static final String NO_RECORD_FOUND = "No record found";

    public static final String CARETAKER_ALREADY_PRESENT= "Caretaker already present";
    public static final String DIV = "</div>\n";
    public static final String SPAN ="</span><br>\n";
    public static final String TD = "</td>\n";
    public static final String STYLE ="<td style='border: 0.5px solid black;text-align: center;vertical-align: middle;height: 40px;'>";
    public static class NullEmptyConstants{
        private NullEmptyConstants(){
            //Do nothing
        }
        public static final String FCM_TOKEN_NULL_EMPTY= "FcmToken cannot be null or empty";
        public static final String TIME_NULL = "Time cannot be null";
        public static final String CARETAKER_NAME_EMPTY = "Caretaker name cannot be empty or null";
        public static final String MED_NAME_EMPTY_NULL = "Medicine name cannot be empty or null";
        public static final String MED_DES_EMPTY_NULL = "Medicine description cannot be empty or null";
        public static final String TITLE_EMPTY_NULL = " Title cannot be empty or null";
        public static final String TAKEN_EMPTY_NULL = "Taken cannot be empty or null";
        public static final String NOT_TAKEN_EMPTY_NULL = "Not Taken cannot be empty or null";
        public static final String DAYS_NULL_EMPTY = "Days cannot be null or empty";
        public static final String TOTAL_REMINDERS_NULL_EMPTY = "Total reminders cannot be null or empty";
        public static final String CURRENT_COUNT_NULL_EMPTY = "Current count cannot be null or empty";
        public static final String PATIENT_NAME_EMPTY_NULL = "Patient name cannot be empty or null";
        public static final String SENT_BY_EMPTY_NULL  = "Sender cannot be null or empty";
        public static final String USER_NAME_EMPTY_NULL = "User name cannot be empty or null";
        public static final String BIO_EMPTY_NULL = "Bio cannot be empty or null";
        public static final String AGE_EMPTY_NULL = "Age cannot be empty or null";
        public static final String CONTACT_EMPTY_NULL = "Contact cannot be empty or null";
        public static final String DATE_NULL_EMPTY = "Date cannot be null or empty";

        public static final String GENDER_EMPTY_NULL = "Gender cannot be empty or null";
        public static final String BLOOD_GROUP_EMPTY_NULL = "Blood group cannot be empty or null";
        public static final String MARITAL_STATUS_EMPTY_NULL = "Marital status cannot be empty or null";
        public static final String WEIGHT_EMPTY_NULL = "Weight cannot be empty or null";
        public static final String PATIENT_ID_EMPTY_NULL= "Patient id cannot be empty or null";
        public static final String CARETAKER_ID_EMPTY_NULL = "Caretaker id cannot be empty or null";
        public static final String ID_EMPTY_NULL="Id cannot be null or empty";
    }

    public  static class LoggerConstants {
        private LoggerConstants() {
        }
        public static final String STARTING_METHOD_EXECUTION = "Starting method execution";
        public static final String EXITING_METHOD_EXECUTION = "Exiting method execution";
        public static final String RESPONSE_SAVED = "Response saved";

    }

}
