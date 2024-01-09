package com.holland.infrastructure.flowchart;

import com.holland.infrastructure.flowchart.anno.Flow;
import com.holland.infrastructure.flowchart.anno.FlowNode;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
public class FlowchartConfig {
    @Resource
    private ConfigurableApplicationContext applicationContext;

    public static Map<String, Set<String>> flowcharts;

    @PostConstruct
    public void registerFlowchart() {
        final Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Flow.class);
        flowcharts = new HashMap<>(beans.size());

        beans.forEach((beanName, object) -> {
            final Flow flow = object.getClass().getAnnotation(Flow.class);

            for (Method method : object.getClass().getMethods()) {
                final FlowNode flowNode = method.getAnnotation(FlowNode.class);
                if (null != flowNode) {
                    flowcharts.computeIfPresent(flow.name(), (k, v) -> {
                        v.add(flowNode.name());
                        return v;
                    });
                    flowcharts.computeIfAbsent(flow.name(), k -> {
                        final Set<String> v = new HashSet<>();
                        v.add(flowNode.name());
                        return v;
                    });
                }
            }
        });
    }
}
