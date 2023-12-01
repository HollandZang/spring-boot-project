package com.holland.infrastructure.flowchart;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlowchartRepositoryConfig {

    @ConditionalOnMissingBean(FlowchartRepository.class)
    @Bean
    public MemoryFlowchartRepository memoryFlowchartRepository() {
        return new MemoryFlowchartRepository();
    }
}
