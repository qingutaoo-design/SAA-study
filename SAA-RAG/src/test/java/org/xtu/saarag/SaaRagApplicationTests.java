package org.xtu.saarag;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.service.collection.request.DropCollectionReq;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SaaRagApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void deleteOldCollection(){
        ConnectConfig connectConfig = ConnectConfig.builder()
                .uri("http://localhost:19530")
                .username("root")
                .password("123456")
                .build();
        
        MilvusClientV2 milvusClient = new MilvusClientV2(connectConfig);
        
        try {

            milvusClient.dropCollection(DropCollectionReq.builder().collectionName("vector_store").build());
            System.out.println("旧集合 vector_store 已删除");
        } catch (Exception e) {
            System.out.println("删除失败或集合不存在: " + e.getMessage());
        } finally {
            milvusClient.close();
        }
    }
}
