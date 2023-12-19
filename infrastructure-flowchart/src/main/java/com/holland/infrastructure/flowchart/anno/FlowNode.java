package com.holland.infrastructure.flowchart.anno;

import com.holland.infrastructure.kit.validator.IsJsonObjValidator;

import javax.validation.Constraint;
import java.lang.annotation.*;

/**
 * 声明对象是一个流程节点
 */
@Documented
@Constraint(validatedBy = IsJsonObjValidator.class)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FlowNode {
    String name();
}
