package com.holland.infrastructure.kit.validator;

import com.alibaba.fastjson2.JSON;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsJsonArrValidator implements ConstraintValidator<IsJsonArr, String> {

    @Override
    public void initialize(IsJsonArr anno) {
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext context) {
        return null == string || JSON.isValidArray(string);
    }
}
