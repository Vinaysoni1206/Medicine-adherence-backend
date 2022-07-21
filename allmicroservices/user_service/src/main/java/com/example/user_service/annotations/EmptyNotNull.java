package com.example.user_service.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static com.example.user_service.util.Constants.FIELD_CANNOT_BE_NULL;


@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmptyNotNullValidator.class)
@Documented
public @interface EmptyNotNull {

    String message() default FIELD_CANNOT_BE_NULL;
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };

}
