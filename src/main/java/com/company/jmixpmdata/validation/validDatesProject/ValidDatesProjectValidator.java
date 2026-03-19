package com.company.jmixpmdata.validation.validDatesProject;

import com.company.jmixpmdata.entity.Project;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class ValidDatesProjectValidator implements ConstraintValidator<ValidDatesProject, Project> {

    @Override
    public boolean isValid(Project project, ConstraintValidatorContext context) {
        if (project == null) {
            return false;
        }

        LocalDateTime startDate = project.getStartDate();
        LocalDateTime endDate = project.getEndDate();

        if (startDate == null || endDate == null) {
            return true;
        }

        return endDate.isAfter(startDate);
    }
}
