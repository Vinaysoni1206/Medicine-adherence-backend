package com.example.user_service.repository;

import com.example.user_service.model.UserCaretaker;
import org.springframework.data.jpa.repository.JpaRepository;

//something
public interface UserCaretakerRepository extends JpaRepository<UserCaretaker, String> {
}