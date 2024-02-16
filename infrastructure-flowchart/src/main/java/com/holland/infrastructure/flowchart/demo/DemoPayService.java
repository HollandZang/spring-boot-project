package com.holland.infrastructure.flowchart.demo;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.holland.infrastructure.flowchart.anno.Flow;
import com.holland.infrastructure.flowchart.anno.Node;
import com.holland.infrastructure.flowchart.domain.Flowchart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Random;

@Flow(name = "示例 - 支付流程")
@Slf4j
public class DemoPayService {
    private final JSONObject 商品和活动 = JSONObject.parseObject("{\n" +
            "  \"苹果\": {\n" +
            "    \"满减活动\": [\n" +
            "      \"100-50\",\n" +
            "      \"50-20\",\n" +
            "      \"20-1\"\n" +
            "    ],\n" +
            "    \"打折活动\": 0.7\n" +
            "  }\n" +
            "}");

    /**
     * 注册 支付流程
     */
    @Autowired
    public DemoPayService(DemoMemoryFlowchartRepository flowchartRepository) {
        flowchartRepository.saveOrUpdate(JSON.parseObject("{\n" +
                "  \"id\": \"示例 - 支付流程\",\n" +
                "  \"name\": null,\n" +
                "  \"flowClass\": null,\n" +
                "  \"linkFromPortIdProperty\": \"fromPort\",\n" +
                "  \"linkToPortIdProperty\": \"toPort\",\n" +
                "  \"nodeDataArray\": [\n" +
                "    {\n" +
                "      \"key\": \"-1\",\n" +
                "      \"category\": \"Start\",\n" +
                "      \"text\": \"开始\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"key\": \"-2\",\n" +
                "      \"category\": \"Step\",\n" +
                "      \"text\": \"示例 - 打折\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"key\": \"-4\",\n" +
                "      \"category\": \"End\",\n" +
                "      \"text\": \"结束\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"key\": \"-5\",\n" +
                "      \"category\": \"Step\",\n" +
                "      \"text\": \"示例 - 满减\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"linkDataArray\": [\n" +
                "    {\n" +
                "      \"from\": \"-1\",\n" +
                "      \"fromPort\": \"B\",\n" +
                "      \"to\": \"-2\",\n" +
                "      \"toPort\": \"T\",\n" +
                "      \"visible\": null\n" +
                "    },\n" +
                "    {\n" +
                "      \"from\": \"-2\",\n" +
                "      \"fromPort\": \"B\",\n" +
                "      \"to\": \"-5\",\n" +
                "      \"toPort\": \"T\",\n" +
                "      \"visible\": null,\n" +
                "      \"points\": []\n" +
                "    },\n" +
                "    {\n" +
                "      \"from\": \"-5\",\n" +
                "      \"fromPort\": \"B\",\n" +
                "      \"to\": \"-4\",\n" +
                "      \"toPort\": \"T\",\n" +
                "      \"visible\": null,\n" +
                "      \"points\": []\n" +
                "    }\n" +
                "  ]\n" +
                "}", Flowchart.class));
    }

    @Node(name = "示例 - 满减")
    public DemoPayReq 满减(DemoPayReq demoPayReq) {
        final String goodsId = demoPayReq.getGoodsId();
        final BigDecimal price = demoPayReq.getFinalPrice();

        final JSONObject 活动 = 商品和活动.getJSONObject(goodsId);
        if (null == 活动) {
            log.info("商品[{}]未参加满减活动", goodsId);
            demoPayReq.getDeductionProcess().add("未参加满减活动");
            return demoPayReq;
        }
        final JSONArray 满减活动 = 活动.getJSONArray("满减活动");
        if (null == 满减活动) {
            log.info("商品[{}]未参加满减活动", goodsId);
            demoPayReq.getDeductionProcess().add("未参加满减活动");
            return demoPayReq;
        }

        // 假设满减金额已排序，从高到低
        for (Object o : 满减活动) {
            final String[] pair = ((String) o).split("-");
            final BigDecimal 满足金额 = new BigDecimal(pair[0]);
            if (price.compareTo(满足金额) < 0)
                continue;

            final BigDecimal 减免金额 = new BigDecimal(pair[1]);

            final BigDecimal finalPrice = price.subtract(减免金额);
            demoPayReq.setFinalPrice(finalPrice);
            demoPayReq.getDeductionProcess().add(String.format("参加满减活动[%s], 价格:%s, 现价:%s", o, price, finalPrice));
            return demoPayReq;
        }

        return demoPayReq;
    }

    @Node(name = "示例 - 代金券")
    public DemoPayReq 代金券(DemoPayReq demoPayReq) {
        final String goodsId = demoPayReq.getGoodsId();
        final BigDecimal price = demoPayReq.getFinalPrice();

        // 假设用户有代金券
        final BigDecimal 用户代金券金额 = BigDecimal.valueOf(new Random().nextInt(20));

        final BigDecimal finalPrice = price.subtract(用户代金券金额);
        demoPayReq.setFinalPrice(finalPrice);
        demoPayReq.getDeductionProcess().add(String.format("使用代金券[%s], 价格:%s, 现价:%s", 用户代金券金额, price, finalPrice));

        return demoPayReq;
    }

    @Node(name = "示例 - 打折")
    public DemoPayReq 打折(DemoPayReq demoPayReq) {
        final String goodsId = demoPayReq.getGoodsId();
        final BigDecimal price = demoPayReq.getFinalPrice();

        final JSONObject 活动 = 商品和活动.getJSONObject(goodsId);
        if (null == 活动) {
            log.info("商品[{}]未参加打折活动", goodsId);
            demoPayReq.getDeductionProcess().add("未参加打折活动");
            return demoPayReq;
        }
        final BigDecimal 打折金额 = 活动.getBigDecimal("打折活动");
        if (null == 打折金额) {
            log.info("商品[{}]未参加打折活动", goodsId);
            demoPayReq.getDeductionProcess().add("未参加打折活动");
            return demoPayReq;
        }

        final BigDecimal finalPrice = price.multiply(打折金额);
        demoPayReq.setFinalPrice(finalPrice);
        demoPayReq.getDeductionProcess().add(String.format("参加打折活动[%s], 价格:%s, 现价:%s", 打折金额, price, finalPrice));

        return demoPayReq;
    }

}
