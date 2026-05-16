package org.xtu.saarag.Controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rag")
public class RagController {

   private VectorStore vectorStore;

    public RagController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }


    @GetMapping("/save")
    public List<Document> save() {
        List <Document> documents = List.of(
                new Document("我是Tzun"),
                new Document("我喜欢Java"),
                new Document("我是一个AI助手"));

// Add the documents to Milvus Vector Store
        vectorStore.add(documents);

// Retrieve documents similar to a query


        return documents;
    }

    @GetMapping("/search")
    public List<Document> search(String query) {
        // 在 Milvus 中测试检索最相关的文档（相似度检索）

        List<Document> results = this.vectorStore.similaritySearch(SearchRequest.builder().query(query).topK(3).build());
        System.out.println(results);
        return results;
    }
}
