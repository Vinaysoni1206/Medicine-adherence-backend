package com.example.user_service.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static com.example.user_service.util.Constants.INVALID_EMAIL;


@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
public @interface Email {
    String message() default INVALID_EMAIL;
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}