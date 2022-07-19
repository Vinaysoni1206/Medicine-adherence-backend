package com.example.user_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.example.user_service.util.Constants.NullEmptyConstants.*;

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
    @NotNull(message = DATE_NULL)
    private String startDate;

    @Column(name = "medicine_name",nullable = false, length = 70)
    @NotNull(message = MED_NAME_NULL)
    @NotEmpty(message = MED_NAME_EMPTY)
    private String medicineName;

    @Column(name = "medicine_des",nullable = false,length = 200)
    @NotNull(message = MED_DES_NULL)
    @NotEmpty(message = MEDDES_EMPTY)
    private String medicineDes;

    @Column(name = "days",nullable = false,length = 4)
    @NotNull(message = DAYS_NULL)
    private String days;

    @Column(name = "end_date",nullable = false)
    @NotNull(message = DATE_NULL)
    private String endDate;

    @Column(name = "time",nullable = false)
    @NotNull(message = TIME_NULL)
    private String time;

    @Column(name = "title",nullable = false, length = 200)
    @NotNull(message = TITLE_NULL)
    @NotEmpty(message = TITLE_EMPTY)
    private String title;

    @Column(name = "total_med_reminders",nullable = false)
    @NotNull(message = TOTAL_REMINDERS_NULL)
    @Range(min = 0)
    private int totalMedReminders;

    @Column(name = "current_count",nullable = false)
    @NotNull(message = CURRENT_COUNT_NULL)
    @Range(min = 0)
    private int currentCount;


    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(
            name = "user_med_id",
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
