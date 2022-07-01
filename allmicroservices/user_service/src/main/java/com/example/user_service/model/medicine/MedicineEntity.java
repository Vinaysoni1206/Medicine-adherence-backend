package com.example.user_service.model.medicine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "medicine")
public class MedicineEntity {

    @Id
    @Column(name = "med_id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private int medId;

    @Column(name = "med_name",nullable = false,length = 70)
    private String medName;

}
