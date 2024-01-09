package com.holland.infrastructure.flowchart;

import com.holland.infrastructure.flowchart.demo.DemoFlowchartConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "holland.flowchart")
public class FlowchartProperties {
    /**
     * 是否开启演示功能
     *
     * @see DemoFlowchartConfig
     * @see com.holland.infrastructure.flowchart.demo.DemoFlowchartController
     * @see com.holland.infrastructure.flowchart.demo.DemoPayController
     */
    private boolean openDemo;
}
