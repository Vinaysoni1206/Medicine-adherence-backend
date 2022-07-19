package com.example.user_service.repository;

import com.example.user_service.model.UserMedicines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * This is a medicine repository class
 */
public interface UserMedicineRepository extends JpaRepository<UserMedicines,Integer> {


    @Query("SELECT u from UserMedicines u where u.medicineId = ?1")
    public UserMedicines getMedById(Integer medicineId);

}
