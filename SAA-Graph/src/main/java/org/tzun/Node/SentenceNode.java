package org.tzun.Node;


import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class SentenceNode implements NodeAction {

    private ChatClient chatClient;

    public SentenceNode(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }


    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {

        //从状态中获取输入
        String value = state.value("word", "");

        //构建PromptTemplate
        PromptTemplate promptTemplate = new PromptTemplate("你是一个英语专家,请对以下内容进行造句：{word}。句子长度在15个单词以内，不要输出其他额外内容");

        //将输入填充到PromptTemplate中
        promptTemplate.add("word", value);

        //渲染PromptTemplate
        String render = promptTemplate.render();

        //调用ChatClient进行对话
        String sentence = chatClient.prompt()
                .user(render)
                .call()
                .content();

        //返回对话结果给状态
        if (sentence == null || sentence.isEmpty()) {
            sentence = "对不起，我无法生成句子。";
        }
        return Map.of("sentence",sentence);
    }
}
