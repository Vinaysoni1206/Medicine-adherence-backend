package com.example.user_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;


import javax.persistence.*;
import java.util.List;
/**
 * This is an entity for storing user medicines
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_medicine", indexes = @Index(name = "index_med" , columnList = "medicine_id"))
public class UserMedicines {

    @Id
    @Column(name = "medicine_id",nullable = false)
    private int medicineId;

    @Column(name = "start_date",nullable = false)
    private String startDate;

    @Column(name = "medicine_name",nullable = false, length = 70)
    private String medicineName;

    @Column(name = "medicine_description",nullable = false,length = 200)
    private String medicineDescription;

    @Column(name = "days",nullable = false,length = 4)
    private String days;

    @Column(name = "end_date",nullable = false)
    private String endDate;

    @Column(name = "time",nullable = false)
    private String time;

    @Column(name = "title",nullable = false, length = 200)
    private String title;

    @Column(name = "total_medicine_reminders",nullable = false)
    @Range(min = 0)
    private int totalMedReminders;

    @Column(name = "current_count",nullable = false)
    @Range(min = 0)
    private int currentCount;


    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(
            name = "user_medicine_id",
            referencedColumnName = "user_id"
    )

    @JsonIgnore
    private User user;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "userMedicines"

    )
    @JsonIgnore
    private List<MedicineHistory> medicineHistories;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "userMedicines"
    )
    @JsonIgnore
    private List<Image> images;

}
