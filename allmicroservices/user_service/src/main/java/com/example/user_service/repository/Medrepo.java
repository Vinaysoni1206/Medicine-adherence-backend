package com.example.user_service.repository;

import com.example.user_service.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @Deprecated
 */
public interface Medrepo extends JpaRepository<Medicine, Integer> {

}
