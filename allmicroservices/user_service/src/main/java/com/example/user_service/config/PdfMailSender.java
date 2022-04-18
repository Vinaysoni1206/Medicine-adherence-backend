package com.example.user_service.config;

import com.example.user_service.model.UserMedicines;
import com.itextpdf.html2pdf.HtmlConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class PdfMailSender {

    @Autowired
    private JavaMailSender javaMailSender;
    Logger logger = LoggerFactory.getLogger(PdfMailSender.class);

    @Async
    public void send(String receiver , List<UserMedicines> userMedicinesList) throws IOException{


        HtmlConverter.convertToPdf(new File(System.getProperty("user.dir")+"/src/main/upload/static/html/pdf-input.html"), new File(System.getProperty("user.dir")+"/src/main/upload/static/pdf/sample.pdf"));

    }

}
