package com.hnclothing.chat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GroqService {

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String ask(String question, String context, String productData) {
        String systemPrompt =
                "Bạn là stylist AI.\n" +
                        "Nhiệm vụ:\n" +
                        "1. Hiểu yêu cầu khách hàng\n" +
                        "2. So sánh với danh sách sản phẩm\n" +
                        "3. Nếu có sản phẩm hơi giống → chọn và giải thích ngắn\n" +
                        "4. Nếu không có → đề xuất sản phẩm tương tự\n" +
                        "5. Nếu khách hỏi chi tiết → trả lời đúng sản phẩm\n\n" +

                        "Lịch sử hội thoại:\n" + context + "\n\n" +

                        "Danh sách sản phẩm:\n" + productData + "\n\n" +

                        "Trả lời NGẮN, đúng trọng tâm.";

        Map<String, String> systemMessage = Map.of("role", "system", "content", systemPrompt);
        Map<String, String> userMessage = Map.of("role", "user", "content", question);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.3-70b-versatile");
        requestBody.put("messages", List.of(systemMessage, userMessage));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
            return "Xin lỗi, tôi không thể phân tích dữ liệu lúc này.";

        } catch (HttpClientErrorException e) {
            System.err.println("=== GROQ API CLIENT ERROR ===");
            System.err.println("Status: " + e.getStatusCode());
            System.err.println("Response Body: " + e.getResponseBodyAsString());
            return "Kết nối AI đang gặp sự cố!";
        } catch (Exception e) {
            System.err.println("=== GROQ API GENERAL ERROR ===");
            e.printStackTrace();
            return "Hệ thống đang bận. Thử lại sau!";
        }
    }
}