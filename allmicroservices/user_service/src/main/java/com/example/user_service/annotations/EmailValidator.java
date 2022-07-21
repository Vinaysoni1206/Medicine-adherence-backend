package com.example.user_service.annotations;

import com.example.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<Email, String> {

    @Autowired
    UserRepository userRepo;

    @Override
    public void initialize(Email constraintAnnotation) {
        //do nothing
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !(s == null || "".equals(s));
    }

}