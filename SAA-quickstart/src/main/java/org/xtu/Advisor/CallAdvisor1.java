package org.xtu.Advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.messaging.handler.annotation.SendTo;

@Slf4j
public class CallAdvisor1 implements CallAdvisor {
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        log.info("请求1");
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        log.info("响应1");
        return chatClientResponse;
    }

    @Override
    public String getName() {
        return "CallAdvisor1";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
