package org.tzun.Controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tzun.Config.GraphConfig;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/graph")
public class GraphController {

    private GraphConfig graphConfig;

    public GraphController(GraphConfig graphConfig) {
        this.graphConfig = graphConfig;
    }

    @GetMapping("/quickStartGraph")
    public String quickStartGraph() throws GraphStateException {

        CompiledGraph compiledGraph = graphConfig.quickStartGraph();

        Optional<OverAllState> invoke = compiledGraph.invoke(Map.of());
        log.info("invoke:{}",invoke);

        return "ok";
    }
}
