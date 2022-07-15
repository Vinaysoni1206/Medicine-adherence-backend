package com.example.user_service.pojos;

import lombok.Data;
import lombok.NoArgsConstructor;

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
