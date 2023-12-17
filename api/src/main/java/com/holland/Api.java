package com.holland;

import com.holland.infrastructure.kit.validator.IsDate;
import com.holland.infrastructure.kit.validator.IsJson;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.holland.infrastructure.kit.enums.Dates.Y;
import static com.holland.infrastructure.kit.enums.Dates.Y_M;

@Validated
@RequestMapping
@RestController
public class Api {
    @Resource
    private TestService testService;

    @Validated
    @RequestMapping("test")
    public String test(
            @IsDate({Y, Y_M}) @IsJson String date
    ) {
        testService.testServ(date);
        return date;
    }

}
