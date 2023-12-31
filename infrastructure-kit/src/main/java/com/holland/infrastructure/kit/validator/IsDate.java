package com.holland.infrastructure.kit.validator;

import com.holland.infrastructure.kit.enums.Dates;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsDateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsDate {

    /**
     * 合法的时间格式列表
     */
    Dates[] value();

    String message() default "不是合法的日期，需要: '{value}'，您输入的是: '{inputVal}'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}