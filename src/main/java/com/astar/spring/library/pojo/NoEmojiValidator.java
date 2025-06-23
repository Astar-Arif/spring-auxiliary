package com.astar.spring.library.pojo;

import com.astar.spring.library.annotation.NoEmoji;
import com.astar.common.library.utils.StringUtility;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoEmojiValidator implements ConstraintValidator<NoEmoji, String> {

    @Override
    public void initialize(NoEmoji constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !StringUtility.isContainEmoji(s);
    }
}
