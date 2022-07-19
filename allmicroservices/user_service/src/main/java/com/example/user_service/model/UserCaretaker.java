package com.example.user_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

import static com.example.user_service.util.Constants.NullEmptyConstants.*;

/**
 * This is an entity for caretaker
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "my_caretakers",indexes = @Index(name = "inedx_fn",columnList = "c_id"))
public class UserCaretaker {

    @Id
    @Column(name = "c_id",nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String cId;

    @Column(name = "patient_name",nullable = false,length = 70)
    @NotEmpty(message = PATIENT_NAME_EMPTY)
    private String patientName;

    @Column(name = "req_status",nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean reqStatus;

    @Column(name = "caretaker_id",nullable = false)
    @NotEmpty(message = CARETAKER_ID_EMPTY)
    private String caretakerId;

    @Column(name = "patient_id",nullable = false)
    @NotEmpty(message = PATIENT_ID_EMPTY)
    private String patientId;

    @Column(name = "caretaker_username",nullable = false,length = 70)
    private String caretakerUsername;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_by",nullable = false,length = 4)
    @NotEmpty(message = SENT_BY_EMPTY)
    private String sentBy;

    @Column(name = "delete",nullable = false)
    private Boolean delete;
    public <D> UserCaretaker(D map) {
    }
}
