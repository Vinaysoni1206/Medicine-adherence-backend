package com.example.user_service.model.image;

import com.example.user_service.model.medicine.UserMedicines;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "image")
public class   Image {

    @Id
    @Column(name = "image_id",nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String imageId;

    @Column(name = "date",nullable = false)
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    @Column(name = "time",nullable = false,length = 50)
    private String time;

    @Column(name = "caretaker_name",nullable = false,length = 60)
    private String caretakerName;

    @Column(name = "image_url",nullable = false,length = 100)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "medimage_id",
            referencedColumnName = "medicine_id"
    )
    @JsonIgnore
    UserMedicines userMedicines;

}
//
