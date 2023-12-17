package com.holland;

import com.holland.infrastructure.kit.validator.IsDate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.holland.infrastructure.kit.enums.Dates.Y;
import static com.holland.infrastructure.kit.enums.Dates.Y_M;

@Validated
@Service
public class TestService {

    public String testServ(
             @IsDate({Y, Y_M}) String date
    ) {
        return date;
    }
}
