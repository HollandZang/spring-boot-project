package com.holland.infrastructure.flowchart.anno;

import com.holland.infrastructure.kit.validator.IsJsonObjValidator;

import javax.validation.Constraint;
import java.lang.annotation.*;

/**
 * 声明对象是一个流程
 */
@Documented
@Constraint(validatedBy = IsJsonObjValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Flow {
    String name();
}
