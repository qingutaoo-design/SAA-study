package org.tzun.Config;

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tzun.Node.SentenceNode;
import org.tzun.Node.TranslationNode;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Configuration
public class GraphConfig {

    @Bean("quickStartGraph")
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



    @Bean("simpleTranslationGraph")
    public CompiledGraph simpleTranslationGraph(ChatClient.Builder chatClientBuilder) throws GraphStateException {

        //定义状态转换策略工厂
        KeyStrategyFactory keyStrategyFactory = () -> Map.of("word",new ReplaceStrategy());
        //创建状态图
        StateGraph stateGraph = new StateGraph("simpleTranslationGraph",keyStrategyFactory);

        //定义节点
        stateGraph.addNode("sentenceNode",AsyncNodeAction.node_async(new SentenceNode(chatClientBuilder)));
        stateGraph.addNode("translationNode",AsyncNodeAction.node_async(new TranslationNode(chatClientBuilder)));

        //定义边

        stateGraph.addEdge(StateGraph.START,"sentenceNode");
        stateGraph.addEdge("sentenceNode","translationNode");
        stateGraph.addEdge("translationNode",StateGraph.END);

        //编译状态图
        return stateGraph.compile();


    }

}
