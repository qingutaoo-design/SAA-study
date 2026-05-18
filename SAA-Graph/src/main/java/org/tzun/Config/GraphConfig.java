package org.tzun.Config;

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Slf4j
@Configuration
public class GraphConfig {

    public CompiledGraph quickStartGraph() throws GraphStateException {
        //定义KeyStrategyFactory
        KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactory() {
            @Override
            public Map<String, KeyStrategy> apply() {
                return Map.of("input1", new ReplaceStrategy()
                        , "input2", new ReplaceStrategy()
                );
            }
        };
        //构建状态图
        StateGraph stateGraph = new StateGraph("my-graph", keyStrategyFactory);

        //定义节点

        stateGraph.addNode("node1", AsyncNodeAction.node_async(new NodeAction() {
                         @Override
                         public Map<String, Object> apply(OverAllState state) throws Exception {
                             log.info("state1:{}", state);
                             return Map.of("input1", 1
                                     , "input2", 1
                             );
                         }
                     }
                )
        );

        stateGraph.addNode("node2", AsyncNodeAction.node_async(new NodeAction() {
                           @Override
                           public Map<String, Object> apply(OverAllState state) throws Exception {
                               log.info("state2:{}", state);
                               return Map.of("input1", 2
                                       , "input2", 2
                               );
                           }
                       }
                )
        );

        //定义边
        stateGraph.addEdge(StateGraph.START,"node1");
        stateGraph.addEdge("node1","node2");
        stateGraph.addEdge("node2",StateGraph.END);

        //编译状态图才可被使用
        return stateGraph.compile();

    }

}
