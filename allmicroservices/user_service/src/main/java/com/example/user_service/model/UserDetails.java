package com.example.user_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
/**
 * This is an entity of User details
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_details",indexes = @Index(name = "inedx_fn",columnList = "user_details_id,user_user_id"))
@ToString(exclude = "")
public class UserDetails {
    @Id
    @Column(name = "user_details_id",nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String userDetailsId;

    @Column(name = "bio",nullable = false,length = 250)
    private String bio;

    @Column(name = "pic_path",nullable = false)
    private String picPath;

    @Column(name = "age",nullable = false,length = 4)
    @Range(min = 1)
    private int age;

    @Column(name = "fcm_token",nullable = false)
    private String fcmToken;

    @Column(name = "pin_code")
    private int pinCode;

    @Column(name = "user_contact",nullable = false)
    @Range(min = 10)
    private Long userContact;

    @Column(name = "latitude")
    private float latitude;

    @Column(name = "longitude")
    private float longitude;

    @Column(name = "address")
    private String address;

    @Column(name = "gender",nullable = false,length = 10)
    private String gender;

    @Column(name = "blood_group",nullable = false,length = 3)
    private String bloodGroup;

    @Column(name = "marital_status",nullable = false, length = 10)
    private String maritalStatus;

    @Column(name = "weight",nullable = false,length = 3)
    @Range(min = 2)
    private int weight;

    @Column(name = "emergency_contact")
    private int emergencyContact;

    @Column(name = "past_medication")
    private String pastMedication;

    @OneToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "user_user_id",
            referencedColumnName = "user_id"
    )
    @JsonIgnore
    private User user;
}
