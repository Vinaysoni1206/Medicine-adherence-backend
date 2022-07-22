package com.example.user_service.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = {ImageValidCheck.class})
public @interface ImageValidation {

     String message() default ("Invalid Image file");
     Class<?>[] groups() default {};

     Class<? extends Payload>[] payload() default {};
}