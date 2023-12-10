package com.holland.infrastructure.kit.validator;

import com.holland.infrastructure.kit.enums.Dates;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class IsDateValidator implements ConstraintValidator<IsDate, String> {

    Pattern pattern;
    String desc;

    @Override
    public void initialize(IsDate anno) {
        final Dates[] dates = anno.value();
        pattern = Pattern.compile(Arrays.stream(dates)
                .map(dateEnum -> dateEnum.validatorPatterns)
                .collect(Collectors.joining("|")));
        desc = Arrays.stream(dates)
                .map(dateEnum -> dateEnum.desc)
                .collect(Collectors.joining(", "));
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext context) {
        if (null == string) return true;

        boolean matches = pattern.matcher(string).matches();
        if (!matches) {

            final String template = context.getDefaultConstraintMessageTemplate();

            final HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .addMessageParameter("value", desc)
                    .addMessageParameter("inputVal", string)
                    .buildConstraintViolationWithTemplate(template)
                    .addConstraintViolation();
        }
        return matches;
    }

}
