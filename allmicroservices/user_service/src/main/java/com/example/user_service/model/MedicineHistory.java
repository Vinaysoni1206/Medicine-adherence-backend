package com.example.user_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
/**
 * This is an entity for storing medicine history
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medicine_history", indexes = @Index(name = "index_medicine_history", columnList = "history_id"))
public class MedicineHistory {

    @Id
    @Column(name = "history_id",nullable = false)
    private int historyId;

    @Column(name = "medicine_date",nullable = false)
    private String date;

    @Column(name = "taken",nullable = false)
    private String taken;

    @Column(name = "not_taken",nullable = false)
    private String notTaken;

    @ManyToOne()
    @JoinColumn(name = "medicine_history", referencedColumnName = "medicine_id")
    @JsonIgnore
    UserMedicines userMedicines;
}
