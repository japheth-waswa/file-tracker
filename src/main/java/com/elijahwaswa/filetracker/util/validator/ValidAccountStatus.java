package com.elijahwaswa.filetracker.util.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AccountStatusValidator.class)
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAccountStatus {
    String message() default "Invalid account status";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
