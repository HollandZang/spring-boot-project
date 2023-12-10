package com.holland.infrastructure.kit.enums;

import lombok.AllArgsConstructor;

@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
@AllArgsConstructor
public enum Dates {
    Y("年", "(^\\d{4}$)"),
    Y_M("年-月", "(^\\d{4}-((0[1-9])|(1[0-2]))$)"),
    Y_M_D("年-月-日", "(^\\d{4}-((0[1-9])|(1[0-2]))-((0[1-9])|([1-2]\\d)|(3[0-1]))$)"),
    ;

    public final String desc;
    public final String validatorPatterns;
}
