package com.example.user_service.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Notificationmessage {
    private String fcmToken;
    private String message;
    private String title;
    private String body;
    private String imageUrl;

    public Notificationmessage(String fcmToken, String message , String title , String body , String imageUrl) {
        this.fcmToken = fcmToken;
        this.message = message;
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;

    }


}
