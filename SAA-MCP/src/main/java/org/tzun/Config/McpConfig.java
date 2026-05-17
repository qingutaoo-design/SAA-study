package org.tzun.Config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tzun.Tool.TimeTool;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider weatherTools(TimeTool tools) {
        return MethodToolCallbackProvider.builder().toolObjects(tools).build();
    }

}
