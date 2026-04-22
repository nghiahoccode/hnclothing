package com.hnclothing.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private GroqService groqService;

    @Autowired
    private ProductQueryService productQueryService;

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody Map<String, String> body) {

        // Lấy thông tin từ gói dữ liệu web gửi lên
        String sessionId = body.getOrDefault("sessionId", "default-session");
        String message = body.get("message");

        // 1. lưu message user
        chatSessionService.addMessage(sessionId, "User: " + message);

        // 2. lấy context
        String context = chatSessionService.buildContext(sessionId);

        // 3. lấy toàn bộ sản phẩm
        String productData = productQueryService.buildProductData();

        // 4. hỏi AI
        String aiResponse = groqService.ask(message, context, productData);

        // 5. lưu response
        chatSessionService.addMessage(sessionId, "AI: " + aiResponse);

        return ResponseEntity.ok(Map.of(
                "reply", aiResponse
        ));
    }
}