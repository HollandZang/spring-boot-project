package com.holland.infrastructure.flowchart;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class FlowchartEngine {

    public final Map<Flow, Set<Node>> flowcharts;

    public FlowchartEngine(ConfigurableApplicationContext applicationContext) {
        final Map<String, Object> beans = applicationContext.getBeansWithAnnotation(com.holland.infrastructure.flowchart.anno.Flow.class);
        flowcharts = new HashMap<>(beans.size());

        beans.forEach((beanName, object) -> {
            Class<?> clazz = object.getClass();
            final com.holland.infrastructure.flowchart.anno.Flow flowAnno = clazz.getAnnotation(com.holland.infrastructure.flowchart.anno.Flow.class);
            final Flow flow = new Flow(flowAnno, clazz, object, clazz.getSimpleName(), clazz.getName(), clazz.getPackage().getName());

            for (Method method : clazz.getMethods()) {
                final com.holland.infrastructure.flowchart.anno.Node nodeAnno = method.getAnnotation(com.holland.infrastructure.flowchart.anno.Node.class);
                if (null != nodeAnno) {
                    flowcharts.computeIfPresent(flow, (k, v) -> {
                        v.add(new Node(nodeAnno, method, method.getName(), method.getParameters()));
                        return v;
                    });
                    flowcharts.computeIfAbsent(flow, k -> {
                        final Set<Node> v = new HashSet<>();
                        v.add(new Node(nodeAnno, method, method.getName(), method.getParameters()));
                        return v;
                    });
                }
            }
        });
    }

    public Set<Node> getNodes(@NotBlank String flowName) {
        for (Map.Entry<Flow, Set<Node>> entry : flowcharts.entrySet()) {
            final Flow flow = entry.getKey();
            if (flow.flowAnno.name().equals(flowName)) {
                return entry.getValue();
            }
        }
        log.warn("输入[{}]，无法找到节点信息", flowName);
        return new HashSet<>();
    }

    @AllArgsConstructor
    public static class Flow {
        public final com.holland.infrastructure.flowchart.anno.Flow flowAnno;
        /**
         * 对象类型
         */
        public final Class<?> clazz;
        /**
         * 对象实例
         */
        public final Object object;
        public final String simpleName;
        public final String fullName;
        public final String packages;
    }

    @AllArgsConstructor
    public static class Node {
        public final com.holland.infrastructure.flowchart.anno.Node nodeAnno;
        public final Method method;
        public final String methodName;
        public final Parameter[] parameters;
    }
}
