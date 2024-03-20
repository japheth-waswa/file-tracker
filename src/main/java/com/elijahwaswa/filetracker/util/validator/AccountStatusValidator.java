package com.elijahwaswa.filetracker.util.validator;

import com.elijahwaswa.filetracker.util.AccountStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AccountStatusValidator implements ConstraintValidator<ValidAccountStatus, AccountStatus> {
    @Override
    public void initialize(ValidAccountStatus constraintAnnotation) {
    }

    @Override
    public boolean isValid(AccountStatus accountStatus, ConstraintValidatorContext constraintValidatorContext) {
       if(accountStatus==null){
           return false;
       }

       for(AccountStatus status:AccountStatus.values()){
           if(status.equals(accountStatus)){
               return true;
           }
       }

       return false;
    }
}
