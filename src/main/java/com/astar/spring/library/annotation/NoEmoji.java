package com.astar.spring.library.annotation;

import com.astar.spring.library.pojo.NoEmojiValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = {NoEmojiValidator.class})
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoEmoji {
    String message() default "Emojis are not allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
