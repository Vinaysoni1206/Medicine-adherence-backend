package com.example.user_service.repository;

import com.example.user_service.model.MedicineHistory;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * This is a medicine history repository class
 */
public interface UserMedHistoryRepository extends JpaRepository<MedicineHistory,Integer> {


}
