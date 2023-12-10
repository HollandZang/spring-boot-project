package com.holland.infrastructure.kit.validator;

import com.alibaba.fastjson2.JSON;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsJsonObjValidator implements ConstraintValidator<IsJsonObj, String> {

    @Override
    public void initialize(IsJsonObj anno) {
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext context) {
        return null == string || JSON.isValidObject(string);
    }
}
