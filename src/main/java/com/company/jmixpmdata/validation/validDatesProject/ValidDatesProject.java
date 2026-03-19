package com.company.jmixpmdata.validation.validDatesProject;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = ValidDatesProjectValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidDatesProject {
    String message() default "Start date can not be after end date";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
