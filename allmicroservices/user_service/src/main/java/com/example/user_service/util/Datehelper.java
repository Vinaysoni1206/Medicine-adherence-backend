package com.example.user_service.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Datehelper {

    public static java.sql.Date getcurrentdate(){
        java.util.Date date=new java.util.Date();

        return new java.sql.Date(date.getTime());

    }

    public static String getcurrentdatatime(){

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);

    }

}
