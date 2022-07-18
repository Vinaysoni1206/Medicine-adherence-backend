package com.example.user_service.util;

public class Messages {


    Messages(){}
    public static final String UNABLE_TO_SYNC = "Unable to sync";
    public static  final String DATA_NOT_FOUND = "Data not found";
    public static final String ERROR = "Error!!";
    public  static  final String DATA_FOUND = "Data found ";

    public static final String SUCCESS = "Success";
    public static final String FAILED = "Failed";
    public static final String SENT_SUCCESS = "Image sent successfully ";
    public static final String MEDICINE_NOT_FOUND = "Medicine not found";
    public static final String USER_ALREADY_PRESENT = "User is already present";
    public static final String NO_RECORD_FOUND = "No record found";

    public static final String CARETAKER_ALREADY_PRESENT= "Caretaker already present";
    public static final String ERROR_TRY_AGAIN = "Error try again!";
    public static class NullEmptyConstants{
        private NullEmptyConstants(){
            //Do nothing
        }
        public static final String TIME_NULL = "Time cannot be null";
        public static final String CARETAKER_NAME_EMPTY = "Caretaker name cannot be empty";
        public static final String MED_NAME_NULL = "Medicine name cannot be null";
        public static final String MED_NAME_EMPTY = "Medicine name cannot be empty";
        public static final String MED_DES_NULL = "Medicine description cannot be null";
        public static final String MEDDES_EMPTY = "Medicine description cannot be empty";
        public static final String TITLE_NULL = " Title cannot be null";
        public static final String TITLE_EMPTY = " Title cannot be empty";
        public static final String DATE_NULL = "Date cannot be null";
        public static final String TAKEN_NULL = "Taken cannot be null";
        public static final String TAKEN_EMPTY = "Taken cannot be empty";
        public static final String NOT_TAKEN_NULL = "Not Taken cannot be null";
        public static final String NOT_TAKEN_EMPTY = "Not Taken cannot be empty";
        public static final String DAYS_NULL = "Days cannot be null";
        public static final String TOTAL_REMINDERS_NULL = "Total reminders cannot be null";
        public static final String CURRENT_COUNT_NULL = "Current count cannot be null";
        public static final String PATIENT_NAME_EMPTY = "Patient name cannot be empty";
        public static final String SENT_BY_EMPTY  = "Sender cannot be null";
        public static final String USER_EMAIL_EMPTY = "User email cannot be empty";
        public static final String USER_NAME_EMPTY = "User name cannot be empty";
        public static final String BIO_EMPTY = "Bio cannot be empty";
        public static final String AGE_EMPTY = "Age cannot be empty";
        public static final String CONTACT_EMPTY = "Contact cannot be empty";
        public static final String GENDER_EMPTY = "Gender cannot be empty";
        public static final String BLOOD_GROUP_EMPTY = "Blood group cannot be empty";
        public static final String MARITAL_STATUS_EMPTY = "Marital status cannot be empty";
        public static final String WEIGHT_EMPTY = "Weight cannot be empty";
        public static final String PATIENT_ID_EMPTY= "Patient id cannot be empty";
        public static final String CARETAKER_ID_EMPTY = "Caretaker id cannot be empty";

    }

    public  static class LoggerConstants {
        private LoggerConstants() {
        }
        public static final String STARTING_METHOD_EXECUTION = "Starting method execution";
        public static final String EXITING_METHOD_EXECUTION = "Exiting method execution";
        public static final String RESPONSE_SAVED = "Response saved";

    }

}
