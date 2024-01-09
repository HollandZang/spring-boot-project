package com.holland.infrastructure.flowchart.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@ConditionalOnProperty("holland.flowchart.open-demo")
@Configuration
public class DemoFlowchartConfig {
    public DemoFlowchartConfig() {
        log.info("Open flowchart demo.");
    }

    @Bean
    public DemoMemoryFlowchartRepository demoMemoryFlowchartRepository() {
        return new DemoMemoryFlowchartRepository();
    }

    @Bean
    public DemoFlowchartController demoFlowchartController() {
        return new DemoFlowchartController();
    }

    @Bean
    public DemoPayController demoPayController() {
        return new DemoPayController();
    }

    @Bean
    public DemoPayService demoPayService() {
        return new DemoPayService(demoMemoryFlowchartRepository());
    }
}
