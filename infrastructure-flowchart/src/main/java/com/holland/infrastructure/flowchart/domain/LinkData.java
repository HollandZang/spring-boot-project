package com.holland.infrastructure.flowchart.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class LinkData {
    private String from;
    private String fromPort;
    private String to;
    private String toPort;
    private String visible;
    private String[] points;
}
