package com.elijahwaswa.filetracker.util.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileNatureValidator.class)
@Target({ElementType.METHOD,ElementType.FIELD,})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFileNature {
    String message() default "Invalid file nature";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
