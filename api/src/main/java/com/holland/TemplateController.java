package com.holland;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TemplateController {

    @GetMapping("/flowchart")
    public String flowchart(Model model, Long id) {
        model.addAttribute("data", "{\n" +
                "  \"class\": \"GraphLinksModel\",\n" +
                "  \"linkFromPortIdProperty\": \"fromPort\",\n" +
                "  \"linkToPortIdProperty\": \"toPort\",\n" +
                "  \"nodeDataArray\": [],\n" +
                "  \"linkDataArray\": []\n" +
                "}");
        return "flowchart";
    }
}