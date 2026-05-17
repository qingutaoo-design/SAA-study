package org.xtu.saarag.Controller;


import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/qa")
public class QAController {


    private VectorStore vectorStore;

    private ChatClient chatClient;

    public QAController(VectorStore vectorStore, ChatClient.Builder chatClient, ToolCallbackProvider toolCallbackProvider) {
        this.vectorStore = vectorStore;
         this.chatClient = chatClient.defaultAdvisors(RetrievalAugmentationAdvisor.builder()
                         .documentRetriever(VectorStoreDocumentRetriever.builder().topK(3).vectorStore(vectorStore).build())
                .build())
                 .defaultToolCallbacks(toolCallbackProvider.getToolCallbacks())
                .build();
    }

    @GetMapping("/import")
    public String importCSV() {
        try {
            // 读取classpath下的QA.csv文件
            ClassPathResource resource = new ClassPathResource("QA.csv");
            InputStreamReader reader = new
                    InputStreamReader(resource.getInputStream());

            // 使用Apache Commons CSV解析CSV文件
            CSVParser csvParser = CSVFormat.DEFAULT
                    .builder()
                    .setHeader()  // 第一行作为标题
                    .setSkipHeaderRecord(true)  // 跳过标题行
                    .build()
                    .parse(reader);

            List<Document> documents = new ArrayList<>();

            // 遍历每一行记录
            for (CSVRecord record : csvParser) {
                // 获取问题和回答字段
                String question = record.get("question");
                String answer = record.get("answer");

                // 将问题和回答组合成文档内容
                String content = "问题: " + question + "\n回答: " + answer;

                // 创建Document对象
                Document document = new Document(content);

                // 添加到文档列表
                documents.add(document);
            }

            // 关闭解析器
            csvParser.close();

            // 将文档存入向量数据库
            vectorStore.add(documents);

            return "成功导入 " + documents.size() + " 条记录到向量数据库";
        } catch (IOException e) {
            e.printStackTrace();
            return "导入失败: " + e.getMessage();
        }
    }

    @GetMapping("/ask")
    public String ask(String query){

        String content = chatClient.prompt()
                .system("你是一个智能助手，帮助用户解答问题。请根据用户的输入提供有用的信息和建议。")
                .user(query)
                .options(ChatOptions.builder()
                        .model("qwen-plus")
                        .build())
                .call()
                .content();
        return content;

    }



}
