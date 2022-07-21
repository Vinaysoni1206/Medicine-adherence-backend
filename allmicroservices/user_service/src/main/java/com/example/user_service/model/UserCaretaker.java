package com.example.user_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * This is an entity for caretaker
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "my_caretakers",indexes = @Index(name = "inedx_fn",columnList = "request_id"))
public class UserCaretaker {

    @Id
    @Column(name = "request_id",nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String requestId;

    @Column(name = "patient_name",nullable = false,length = 70)
    private String patientName;

    @Column(name = "request_status",nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean requestStatus;

    @Column(name = "caretaker_id",nullable = false)
    private String caretakerId;

    @Column(name = "patient_id",nullable = false)
    private String patientId;

    @Column(name = "caretaker_username",nullable = false,length = 70)
    private String caretakerUsername;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_by",nullable = false,length = 4)
    private String sentBy;

    @Column(name = "delete",nullable = false)
    private Boolean delete;
    public <D> UserCaretaker(D map) {
    }
}
