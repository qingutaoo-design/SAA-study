package org.tzun.Node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;
@Slf4j
public class LoopEvaluateJokesNode implements NodeAction {
    private final ChatClient chatClient;
    private  int goalScore;
    private int loopCount;
    public LoopEvaluateJokesNode(ChatClient.Builder builder, int goalScore, int loopCount) {
        this.chatClient = builder.build();
        this.goalScore = goalScore;
        this.loopCount = loopCount;
    }
    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        // 从state 中获取
        String joke = state.value("joke", "");
        PromptTemplate promptTemplate = new PromptTemplate("你是一个笑话评分专家，能够对笑话进行评分，基于效果的搞笑程度给出0到10分的打分。" +
                "要求结果只返回最后的评分，不要其他内容。" +
                "要评分的笑话:{joke}");
        promptTemplate.add("joke",joke);
        String prompt = promptTemplate.render();
        // 模型调用
        String content = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        // 获取分数
        int score = Integer.parseInt(content.trim());
        //拿到本次循环的次数
        int count = state.value("count", 1);
        String nextStep = "break";

        if(score < goalScore && count < loopCount){
            nextStep = "loop";

        }
        log.info("score {},  count {}",score,count);
        log.info("joke {}",joke);
        count++;
        return Map.of("nextStep",nextStep,"count",count);
    }
}