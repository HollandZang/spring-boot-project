package com.holland.infrastructure.flowchart.demo;

import com.alibaba.fastjson2.JSON;
import com.holland.infrastructure.flowchart.FlowchartEngine;
import com.holland.infrastructure.flowchart.FlowchartRepository;
import com.holland.infrastructure.flowchart.domain.Flowchart;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("demo")
public class DemoFlowchartController {
    private static final String INFO = "/flowchart/info";
    private static final String SAVE_OR_UPDATE = "/flowchart/saveOrUpdate";

    @Resource
    private FlowchartRepository<String> flowchartRepository;
    @Resource
    private FlowchartEngine flowchartEngine;

    @GetMapping(INFO)
    public String info(Model model, HttpServletRequest request, String id) {
        final Flowchart<String> flowchart = StringUtils.hasLength(id)
                ? flowchartRepository.info(id)
                : Flowchart.newEmptyInst();
        model.addAttribute("data", JSON.toJSONString(flowchart));

        final String url = request.getRequestURL().toString();
        final String base = url.substring(0, url.length() - INFO.length());
        model.addAttribute("req_url_info", url);
        model.addAttribute("req_url_saveOrUpdate", base + SAVE_OR_UPDATE);
        final Set<String> flowNodes = flowchartEngine.getNodes(id)
                .stream()
                .map(node -> node.methodName)
                .collect(Collectors.toSet());
        model.addAttribute("flowNodes", JSON.toJSONString(flowNodes));

        return "flowchart";
    }

    @PostMapping(SAVE_OR_UPDATE)
    public ResponseEntity<Flowchart<String>> saveOrUpdate(@RequestBody Flowchart<String> flowchart) {
        final Flowchart<String> newOne = flowchartRepository.saveOrUpdate(flowchart);
        return ResponseEntity.ok(newOne);
    }

}