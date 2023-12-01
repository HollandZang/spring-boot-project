package com.holland.infrastructure.flowchart;

import com.holland.infrastructure.flowchart.domain.Flowchart;
import org.springframework.lang.NonNull;

/**
 * 流程图 存储库
 */
public interface FlowchartRepository<ID> {
    Flowchart<ID> info(@NonNull ID id);

    Flowchart<ID> saveOrUpdate(Flowchart<ID> flowchart);
}
