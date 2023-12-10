package com.holland;

import com.holland.infrastructure.kit.validator.IsDate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.holland.infrastructure.kit.enums.Dates.Y;
import static com.holland.infrastructure.kit.enums.Dates.Y_M;

@Validated
@RequestMapping
@RestController
public class Api {

    @Validated
    @RequestMapping("test")
    public String test(
            @Validated @IsDate({Y, Y_M}) String date
    ) {
        return date;
    }
}
