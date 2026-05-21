package org.xtu.Repository;

import cn.hutool.json.JSONUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RedisChatMemory implements ChatMemoryRepository {

    private final StringRedisTemplate redisTemplate;

    public RedisChatMemory(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }



    @Override
    public List<String> findConversationIds() {
        List<String> keys = redisTemplate.keys("conversation:*").stream().collect(Collectors.toList());
        if(keys.isEmpty()){
            return new ArrayList<String>();
        }
        return keys;
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {

        List<Message> messages = new ArrayList<>();
          List<String> jsonMessages = redisTemplate.opsForList().range(conversationId, 0, -1);
  // 然后遍历 jsonMessages 进行反序列化
        for (String jsonMessage : jsonMessages) {
            Message message = JSONUtil.toBean(jsonMessage, Message.class);
            messages.add(message);
        }
        return messages;
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }


        List<String> jsonMessages = messages.stream().map(msg -> {
            try {
                return JSONUtil.toJsonStr(msg);
            } catch (Exception e) {
                e.printStackTrace();
                return null; // 或者抛出一个运行时异常
            }
        }).collect(Collectors.toList());

        // 使用批量插入优化性能
        redisTemplate.opsForList().rightPushAll(conversationId, jsonMessages);
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        redisTemplate.delete(conversationId);
    }
}
