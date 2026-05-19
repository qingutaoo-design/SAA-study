package org.tzun.Controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/graph")
public class GraphController {

    private CompiledGraph compiledGraph1;
    private CompiledGraph compiledGraph2;
    private CompiledGraph compiledGraph3;

    public GraphController(@Qualifier("quickStartGraph") CompiledGraph compiledGraph1,
                           @Qualifier("simpleTranslationGraph") CompiledGraph compiledGraph2,
                           @Qualifier("simpleConditionGraph")CompiledGraph compiledGraph3) {
        this.compiledGraph1 = compiledGraph1;
        this.compiledGraph2 = compiledGraph2;
        this.compiledGraph3 = compiledGraph3;
    }

    @GetMapping("/quickStartGraph")
    public String quickStartGraph() throws GraphStateException {
        Optional<OverAllState> invoke = compiledGraph1.invoke(Map.of());
        log.info("invoke:{}",invoke);

        return "ok";
    }

    @GetMapping("/quickStartGraph2")
    public Map<String, Object> quickStartGraph2(String query) throws GraphStateException {
        Optional<OverAllState> overAllState = compiledGraph2.invoke(Map.of("word", query));
        return overAllState.map(state -> state.data())
                .orElse(Map.of());

    }


    @GetMapping("/quickStartGraph3")
    public Map<String, Object> quickStartGraph3(String query) throws GraphStateException {
        Optional<OverAllState> overAllState = compiledGraph3.invoke(Map.of("topic", query));
        return overAllState.map(state -> state.data())
                .orElse(Map.of());
    }
}
