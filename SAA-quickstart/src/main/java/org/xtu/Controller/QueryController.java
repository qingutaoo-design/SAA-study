package org.xtu.Controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/query")
public class QueryController {


    private ChatModel chatModel;

    public QueryController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping()
    public String query(@RequestBody String query) {
        System.out.println("Received query: " + query);

        // 使用 ChatModel 调用 AI 模型
        String response = chatModel.call(query);

        return response;
    }
}
