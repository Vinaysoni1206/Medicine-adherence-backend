package com.example.user_service.repository;

import com.example.user_service.model.user.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserDetailsRepository extends JpaRepository<UserDetails, Integer> {

}
