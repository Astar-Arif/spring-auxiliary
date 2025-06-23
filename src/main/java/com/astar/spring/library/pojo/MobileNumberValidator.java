package com.astar.spring.library.pojo;

import com.astar.spring.library.annotation.MobileNumber;
import com.astar.common.library.utils.StringUtility;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MobileNumberValidator implements ConstraintValidator<MobileNumber, String> {

    @Override
    public void initialize(MobileNumber constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return StringUtility.isMobileNumber(s);
    }
}
