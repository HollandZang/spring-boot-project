package com.holland.infrastructure.flowchart.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程图 对象实例
 */
@Getter
@Setter
@Accessors(chain = true)
public class Flowchart<ID> {
    private ID id;
    private String name;

    private String flowClass;
    private String linkFromPortIdProperty;
    private String linkToPortIdProperty;
    private List<NodeData> nodeDataArray;
    private List<LinkData> linkDataArray;

    public static <ID> Flowchart<ID> newEmptyInst() {
        return new Flowchart<ID>()
                .setFlowClass("GraphLinksModel")
                .setLinkFromPortIdProperty("fromPort")
                .setLinkToPortIdProperty("toPort")
                .setNodeDataArray(new ArrayList<>())
                .setLinkDataArray(new ArrayList<>())
                ;
    }
}
