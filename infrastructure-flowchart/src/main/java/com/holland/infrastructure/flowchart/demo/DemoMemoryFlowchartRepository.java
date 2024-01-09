package com.holland.infrastructure.flowchart.demo;

import com.holland.infrastructure.flowchart.FlowchartRepository;
import com.holland.infrastructure.flowchart.domain.Flowchart;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.UUID;

/**
 * 基于内存的流程图存储实现
 */
public class DemoMemoryFlowchartRepository implements FlowchartRepository<String> {
    private final HashMap<String, Flowchart<String>> memory = new HashMap<>();

    @Override
    public Flowchart<String> info(@NonNull String id) {
        return memory.get(id);
    }

    @Override
    public Flowchart<String> saveOrUpdate(Flowchart<String> flowchart) {
        String id = flowchart.getId();
        if (!StringUtils.hasLength(id)) {
            id = UUID.randomUUID().toString();
            flowchart.setId(id);
        }

        memory.put(id, flowchart);

        return flowchart;
    }
}
