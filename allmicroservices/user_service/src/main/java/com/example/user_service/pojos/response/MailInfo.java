package com.example.user_service.pojos.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a response class for a mail
 */
@Data
@NoArgsConstructor
public class MailInfo {

    private String receiverMail;
    private String mailMessage;
    private String mailSubject;
    private String sender;

    public MailInfo(String receiverMail ,
                    String mailMessage , String mailSubject,String sender){

        this.receiverMail = receiverMail;
        this.mailMessage = mailMessage;
        this.mailSubject = mailSubject;
        this.sender = sender;
    }
}
