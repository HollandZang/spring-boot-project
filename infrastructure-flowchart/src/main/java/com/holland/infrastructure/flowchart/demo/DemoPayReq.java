package com.holland.infrastructure.flowchart.demo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Accessors(chain = true)
@Getter
@Setter
public class DemoPayReq {
    private String goodsId;
    private BigDecimal price;

    private List<String> deductionProcess = new ArrayList<>();
    private BigDecimal finalPrice;

    public BigDecimal getFinalPrice() {
        return null != finalPrice ? finalPrice : price;
    }
}
