package com.holland.infrastructure.flowchart.demo;

import com.holland.infrastructure.flowchart.FlowchartRepository;
import com.holland.infrastructure.flowchart.anno.Flow;
import com.holland.infrastructure.flowchart.anno.FlowNode;
import com.holland.infrastructure.flowchart.domain.Flowchart;
import com.holland.infrastructure.flowchart.domain.LinkData;
import com.holland.infrastructure.flowchart.domain.NodeData;
import com.holland.infrastructure.kit.web.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("demo/pay")
@RestController
public class DemoPayController {
    @Resource
    private FlowchartRepository<String> flowchartRepository;
    @Resource
    private DemoPayService demoPayService;

    @PostMapping("buyGoods")
    public R<?> buyGoods(@RequestBody DemoPayReq demoPayReq) throws InvocationTargetException, IllegalAccessException {
        // todo 下面的代码都可以抽象为一个流程引擎
        final Class<DemoPayService> demoPayServiceClass = DemoPayService.class;
        final String flowchartName = demoPayServiceClass.getAnnotation(Flow.class).name();
        final Map<String, Method> nameMethodMap = new HashMap<>();
        for (Method method : demoPayServiceClass.getMethods()) {
            final FlowNode flowNode = method.getAnnotation(FlowNode.class);
            if (null != flowNode) {
                nameMethodMap.put(flowNode.name(), method);
            }
        }

        final Flowchart<String> flowchart = flowchartRepository.info(flowchartName);

        final List<NodeData> nodeDataArray = flowchart.getNodeDataArray();
        final List<LinkData> linkDataArray = flowchart.getLinkDataArray();

        final NodeData startNode = nodeDataArray.stream().filter(nodeData -> "Start".equals(nodeData.getCategory())).findFirst().get();

        NodeData currNode = startNode;
        // 使用 i 用于保证不会出现死循环
        for (int i = 0; i < nodeDataArray.size(); i++) {
            NodeData finalCurrNode = currNode;
            final List<LinkData> collect = linkDataArray.stream().filter(linkData -> finalCurrNode.getKey().equals(linkData.getFrom()))
                    .sorted() // todo 根据优先权排序
                    .collect(Collectors.toList());
            // 假设第一个就满足条件
            final LinkData linkData = collect.get(0);
            // 找到下一个节点
            final NodeData execNode = nodeDataArray.stream().filter(nodeData -> linkData.getTo().equals(nodeData.getKey())).findFirst().get();
            if ("End".equals(execNode.getCategory()))
                break;

            final String text = execNode.getText();
            final Method method = nameMethodMap.get(text);
            demoPayReq = (DemoPayReq) method.invoke(demoPayService, demoPayReq);
            currNode = execNode;
        }

        return R.ok(demoPayReq);
    }
}
