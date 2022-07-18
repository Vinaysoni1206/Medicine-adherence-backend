package com.example.user_service.model.medicine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.example.user_service.util.Messages.NullEmptyConstants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "medicine", indexes = @Index(name = "index_medId" , columnList = "med_id"))
public class MedicineEntity {

    @Id
    @Column(name = "med_id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private int medId;

    @Column(name = "med_name",nullable = false,length = 70)
    @NotNull(message = MED_NAME_NULL)
    @NotEmpty(message = MED_NAME_EMPTY)
    private String medName;

}
