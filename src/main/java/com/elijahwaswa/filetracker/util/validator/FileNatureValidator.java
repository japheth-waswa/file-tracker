package com.elijahwaswa.filetracker.util.validator;

import com.elijahwaswa.filetracker.util.FileNature;
import com.elijahwaswa.filetracker.util.FileStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileNatureValidator implements ConstraintValidator<ValidFileNature, FileNature> {
    @Override
    public void initialize(ValidFileNature constraintAnnotation) {
    }

    @Override
    public boolean isValid(FileNature fileNature, ConstraintValidatorContext constraintValidatorContext) {
        if (fileNature == null) {
            return false;
        }

        for (FileNature status : FileNature.values()) {
            if (status.equals(fileNature)) {
                return true;
            }
        }
        return false;
    }

}
