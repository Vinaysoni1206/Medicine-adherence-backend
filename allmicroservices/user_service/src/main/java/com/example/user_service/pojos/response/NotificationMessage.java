package com.example.user_service.pojos.response;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a response class for notification message
 */
@Data
@NoArgsConstructor
public class NotificationMessage {
    private String fcmToken;
    private String message;
    private String title;
    private String body;
    private String imageUrl;
    public NotificationMessage(String fcmToken, String message , String title , String body , String imageUrl) {
        this.fcmToken = fcmToken;
        this.message = message;
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;

    }


}
