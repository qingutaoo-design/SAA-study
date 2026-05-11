package org.xtu.Advisor;


import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleBaseAdvisor implements BaseAdvisor {

    private static Map<String, List<Message>> historyMap = new ConcurrentHashMap<>(); // 存储每个对话ID对应的历史对话记录

    //实现将历史对话传递给LLM的功能
    //保证每个id是为一个独立的对话
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        // 获取当前对话ID
        // 先简单设置一个固定的对话ID，实际使用中可以根据请求内容生成唯一ID
        String conversationId = chatClientRequest.context().get("conversationId").toString();// String conversationId = "id-1";
        // 从历史记录中获取当前对话ID对应的历史对话记录 (使用 Spring 的 Message 类型)
        List<Message> chatMemory = historyMap.get(conversationId);
        if (chatMemory == null) {
            // 如果没有历史记录，则创建一个新的空列表
            chatMemory = new ArrayList<>();
        }

        // 读取当前请求中的消息（通常为 org.springframework.ai.chat.chatMemory.Message 列表）并追加到历史记录
        Prompt prompt = chatClientRequest.prompt();
        if (prompt != null) {
            List<Message> messageList = prompt.getInstructions();
            if (messageList != null && !messageList.isEmpty()) {
                chatMemory.addAll(messageList);
            }

            // 将添加了历史对话的请求替换掉当前请求中的消息列表，传递给LLM
            Prompt newPrompt = prompt.mutate().messages(chatMemory).build();

            // 保存当前消息给历史记录
            historyMap.put(conversationId, chatMemory);

            return ChatClientRequest.builder()
                    .prompt(newPrompt)
                    .build();
        }
        // 如果没有 prompt，则直接返回原请求
        return chatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        // 对LLM的响应进行处理，例如将LLM的回复添加到历史对话中

        // 获取当前对话ID
        String conversationId = chatClientResponse.context().get("conversationId").toString(); // String conversationId = "id-1";

        // 从历史记录中获取当前对话ID对应的历史对话记录
        List<Message> chatMemory = historyMap.get(conversationId);
        if (chatMemory == null) {
            chatMemory = new ArrayList<>();
        }
        AssistantMessage output = chatClientResponse.chatResponse().getResult().getOutput();
        if (output != null) {
            // 将LLM的回复添加到历史对话中
            chatMemory.add(output);
            // 更新历史记录
            historyMap.put(conversationId, chatMemory);
        }

        System.out.println(chatMemory);
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
