package org.xtu.saarag.Controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rag")
public class RagController {

   private VectorStore vectorStore;

    public RagController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }


    @GetMapping("/save")
    public String save() {
        List <Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")));

// Add the documents to Milvus Vector Store
        vectorStore.add(documents);

// Retrieve documents similar to a query


        return "数据已成功保存到 Milvus 向量数据库中！";
    }

    @GetMapping("/search")
    public List<Document> search(String query) {
        // 在 Milvus 中测试检索最相关的文档（相似度检索）

        List<Document> results = this.vectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(5).build());
        System.out.println(results);
        return null;
    }
}
