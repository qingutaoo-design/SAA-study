package org.tzun.Node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class TranslationNode implements NodeAction {

    private ChatClient chatClient;

    public TranslationNode(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }


    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {

        //从状态中获取输入
        String value = state.value("sentence", "");

        //构建PromptTemplate
        PromptTemplate promptTemplate = new PromptTemplate("你是一个英语专家,请对以下内容进行翻译：{sentence}。只需要翻译句子即可，不要输出其他额外内容");

        //将输入填充到PromptTemplate中
        promptTemplate.add("sentence", value);

        //渲染PromptTemplate
        String render = promptTemplate.render();

        //调用ChatClient进行对话
        String translation = chatClient.prompt()
                .user(render)
                .call()
                .content();

        //返回对话结果给状态
        if (translation == null || translation.isEmpty()) {
            translation = "对不起，我无法翻译。";
        }
        return Map.of("translation",translation);
    }
}
