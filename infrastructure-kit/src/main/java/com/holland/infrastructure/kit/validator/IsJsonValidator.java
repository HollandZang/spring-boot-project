package com.holland.infrastructure.kit.validator;

import com.alibaba.fastjson2.JSON;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsJsonValidator implements ConstraintValidator<IsJson, String> {

    @Override
    public void initialize(IsJson anno) {
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext context) {
        return null == string || JSON.isValid(string);
    }
}
