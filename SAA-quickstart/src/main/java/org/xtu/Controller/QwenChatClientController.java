package org.xtu.Controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.*;
import org.xtu.Advisor.CallAdvisor1;
import org.xtu.Advisor.CallAdvisor2;
import org.xtu.Advisor.SimpleBaseAdvisor;
import org.xtu.Entity.Book;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@RestController
@RequestMapping("/qwen/chatclient")
public class QwenChatClientController {

    private final ChatClient chatClient;

    public QwenChatClientController(ChatClient.Builder builder) {
            this.chatClient = builder.build();
    }


    @GetMapping("/query")
    public String query(@RequestBody String query) {
        ChatClient.CallResponseSpec responseSpec = chatClient.prompt()
                .system("你是一个智能助手，帮助用户解答问题。请根据用户的输入提供有用的信息和建议。")
                .user(query)
                .options(DashScopeChatOptions.builder()
                        .temperature(0.0)
                        .model("qwen-plus")
                        .maxToken(2048)
                        .build())
                .call();
        return responseSpec.content();
    }

    @GetMapping("/query2")
    public String query2(@RequestBody String query) {
        Book entity = chatClient.prompt()
                .system("你是一个智能助手，帮助用户解答问题。请根据用户的输入提供有用的信息和建议。")
                .user(query)
                .options(DashScopeChatOptions.builder()
                        .temperature(0.0)
                        .model("qwen-plus")
                        .maxToken(2048)
                        .build())
                .call()
                .entity(Book.class);
        return entity.toString();
    }

    @GetMapping("/stream")
    public Flux<String> stream(@RequestParam String query) {
        Flux<String> content = chatClient.prompt()
                .system("你是一个智能助手，帮助用户解答问题。请根据用户的输入提供有用的信息和建议。")
                .user(query)
                .options(DashScopeChatOptions.builder()
                        .temperature(0.0)
                        .model("qwen-plus")
                        .maxToken(2048)
                        .build())
                .stream()
                .content();
        return content;
    }

    @GetMapping("/advisor")
    public String advisor(@RequestBody String query) {
        Book entity = chatClient.prompt()
                .system("你是一个智能助手，帮助用户解答问题。请根据用户的输入提供有用的信息和建议。")
                .user(query)
                .options(DashScopeChatOptions.builder()
                        .temperature(0.0)
                        .model("qwen-plus")
                        .maxToken(2048)
                        .build())
                .advisors(new CallAdvisor1(),new CallAdvisor2())
                .call()
                .entity(Book.class);
        return entity.toString();
    }

    @GetMapping("/simpleBaseAdvisor")
    public String SimpleBaseAdvisor(@RequestParam String query,@RequestParam String conversationId) {
        String content = chatClient.prompt()
                .system("你是一个智能助手，帮助用户解答问题。请根据用户的输入提供有用的信息和建议。")
                .user(query)
                .options(DashScopeChatOptions.builder()
                        .temperature(0.0)
                        .model("qwen-plus")
                        .maxToken(2048)
                        .build())
                .advisors(advisorSpec -> advisorSpec.param("conversationId",conversationId))
                .advisors(new SimpleBaseAdvisor())
                .call()
                .content();
        return content;
    }

}
