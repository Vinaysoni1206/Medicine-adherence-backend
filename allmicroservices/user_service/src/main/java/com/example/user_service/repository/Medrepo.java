package com.example.user_service.repository;

import com.example.user_service.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Deprecated
 */
public interface Medrepo extends JpaRepository<Medicine, Integer> {

}
