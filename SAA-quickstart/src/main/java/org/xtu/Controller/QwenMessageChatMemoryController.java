package org.xtu.Controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.web.bind.annotation.*;
import org.xtu.Advisor.SimpleBaseAdvisor;

@RestController
@RequestMapping("/qwen/chatMemory")
public class QwenMessageChatMemoryController {

    private final ChatClient chatClient;

    // 使用统一的advisor
    // 在这个示例中，我们创建了一个MessageWindowChatMemory实例，并使用它来构建一个MessageChatMemoryAdvisor。
    // 然后，我们将这个advisor作为默认advisor传递给ChatClient.Builder，这样在调用chatClient.prompt()时就会自动使用这个advisor来处理聊天记忆。
    public QwenMessageChatMemoryController(ChatClient.Builder builder) {

        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .build();

        // 使用MessageChatMemoryAdvisor
        MessageChatMemoryAdvisor memoryAdvisor= MessageChatMemoryAdvisor.builder(chatMemory)
                .build();

        this.chatClient = builder
                    .defaultAdvisors(memoryAdvisor)
                    .build();
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

    //不用自定义的advisor
    @GetMapping("/simpleMessageChatMemoryAdvisor")
    public String SimpleMessageChatMemoryAdvisor(@RequestParam String query) {
        String content = chatClient.prompt()
                .system("你是一个智能助手，帮助用户解答问题。请根据用户的输入提供有用的信息和建议。")
                .user(query)
                .options(DashScopeChatOptions.builder()
                        .temperature(0.0)
                        .model("qwen-plus")
                        .maxToken(2048)
                        .build())
                .call()
                .content();
        return content;
    }

}
