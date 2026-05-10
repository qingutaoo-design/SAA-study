package org.xtu.Controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/qwen")
public class QwenChatController {


    private ChatModel chatModel;

    public QwenChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/query")
    public String query1(@RequestBody String query) {
        System.out.println("Received query: " + query);

        // 使用 ChatModel 调用 AI 模型
        String response = chatModel.call(query);

        return response;
    }

    @GetMapping("/message")
    public String query2(@RequestBody String query) {
        System.out.println("Received query: " + query);
        SystemMessage systemMessage = new SystemMessage("你是一个智能助手，帮助用户解答问题。请根据用户的输入提供有用的信息和建议。");
        UserMessage userMessage = new UserMessage(query);
        // 使用 ChatModel 调用 AI 模型
        String response = chatModel.call(systemMessage, userMessage);
        return response;
    }

    @GetMapping("/chatoption")
    public String query3(@RequestBody String query) {
        System.out.println("Received query: " + query);
        SystemMessage systemMessage = new SystemMessage("你是一个智能助手，帮助用户解答问题。请根据用户的输入提供有用的信息和建议。");
        UserMessage userMessage = new UserMessage(query);

        DashScopeChatOptions chatoption = DashScopeChatOptions.builder()
                .temperature(0.0)
                .model("qwen-plus")
                .maxToken(2048)
                .build();

        // 使用 ChatModel 调用 AI 模型
        ChatResponse response = chatModel.call(new Prompt(List.of(systemMessage, userMessage), chatoption));

        String text = response.getResult().getOutput().getText();

        return text;
    }

    @GetMapping("/stream/query")
    public Flux<String> query4(@RequestParam String query){
        Flux<String> stream = chatModel.stream(query);
        return stream;
    }
}
