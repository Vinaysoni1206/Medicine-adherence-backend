package com.example.user_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
/**
 * This is an entity for medicine
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "medicine", indexes = @Index(name = "index_medicine_id" , columnList = "medicine_id"))
public class Medicine {

    @Id
    @Column(name = "medicine_id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private int medId;

    @Column(name = "medicine_name",nullable = false,length = 70)
    private String medName;

}
