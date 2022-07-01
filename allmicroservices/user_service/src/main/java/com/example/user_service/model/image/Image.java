package com.example.user_service.model.image;

import com.example.user_service.model.medicine.UserMedicines;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "image")
public class Image {

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
    @NotNull(message = "Date cannot be empty")
    private Date date;

    @Column(name = "time",nullable = false,length = 50)
    @NotNull(message = "Time cannot be empty")
    @NotEmpty(message = "Time cannot be empty")
    private String time;

    @Column(name = "Caretaker_name",nullable = false,length = 60)
    @NotEmpty(message = "Caretaker name cannot be empty")
    @NotNull(message = "Caretaker name cannot be empty")
    private String caretakerName;

    @Column(name = "image_url",nullable = false,length = 100)
    @NotNull(message = "Image url cannot be empty")
    @NotEmpty(message = "Image url cannot be empty")
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
