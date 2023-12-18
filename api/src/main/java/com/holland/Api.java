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

    @Resource
    private TestService1 testService1;

    @RequestMapping("contextLoads")
    void contextLoads() throws InterruptedException {
        System.out.printf("第一次调用，使用的应该是 计算：%s\n\n", testService1.test());

        System.out.printf("3秒内，使用的应该是 caffeine：%s\n\n", testService1.test());
        Thread.sleep(3000);
        System.out.printf("3秒后，使用的应该是 redis：%s\n\n", testService1.test());
        Thread.sleep(3000);
        System.out.printf("6秒后，使用的应该是 计算：%s\n\n", testService1.test());
    }
}
