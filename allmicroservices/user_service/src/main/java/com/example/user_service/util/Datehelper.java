package com.example.user_service.util;

import java.time.LocalDateTime;

public class Datehelper {

    Datehelper(){}
    public static LocalDateTime getcurrentdatatime(){

        LocalDateTime now = LocalDateTime.now();
        return (now);

    }

}
