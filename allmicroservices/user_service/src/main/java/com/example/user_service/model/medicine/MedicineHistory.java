package com.example.user_service.model.medicine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.example.user_service.util.Messages.NullEmptyConstants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medicine_history", indexes = @Index(name = "index_medHis", columnList = "history_id"))
public class MedicineHistory {

    @Id
    @Column(name = "history_id",nullable = false)
    private int historyId;

    @Column(name = "med_date",nullable = false)
    @NotNull(message = DATE_NULL)
    private String date;

    @Column(name = "taken",nullable = false)
    @NotNull(message = TAKEN_NULL)
    @NotEmpty(message = TAKEN_EMPTY)
    private String taken;

    @Column(name = "not_taken",nullable = false)
    @NotNull(message = NOT_TAKEN_NULL)
    @NotEmpty(message = NOT_TAKEN_EMPTY)
    private String notTaken;

    @ManyToOne()
    @JoinColumn(name = "medicine_history", referencedColumnName = "medicine_id")
    @JsonIgnore
    UserMedicines userMedicines;
}
