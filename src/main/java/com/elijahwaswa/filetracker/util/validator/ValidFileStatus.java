package com.elijahwaswa.filetracker.util.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileStatusValidator.class)
@Target({ElementType.METHOD,ElementType.FIELD,})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFileStatus {
    String message() default "Invalid file status";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
