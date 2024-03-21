package com.elijahwaswa.filetracker.util.validator;

import com.elijahwaswa.filetracker.util.AccountStatus;
import com.elijahwaswa.filetracker.util.FileStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileStatusValidator implements ConstraintValidator<ValidFileStatus, FileStatus> {
    @Override
    public void initialize(ValidFileStatus constraintAnnotation) {
    }

    @Override
    public boolean isValid(FileStatus fileStatus, ConstraintValidatorContext constraintValidatorContext) {
        if (fileStatus == null) {
            return false;
        }

        for (FileStatus status : FileStatus.values()) {
            if (status.equals(fileStatus)) {
                return true;
            }
        }
        return false;
    }

}
