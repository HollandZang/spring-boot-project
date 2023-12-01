package com.holland.infrastructure.flowchart.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class NodeData {
    private String key;
    private String category;
    private String text;
    /**
     * location
     */
    private String loc;
}
