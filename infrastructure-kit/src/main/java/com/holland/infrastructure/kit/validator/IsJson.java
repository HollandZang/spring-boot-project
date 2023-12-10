package com.holland.infrastructure.kit.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsJsonValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsJson {
    String message() default "not a JSON，your value: '${validatedValue}'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}