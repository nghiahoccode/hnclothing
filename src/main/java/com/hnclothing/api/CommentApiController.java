package com.hnclothing.api;

import com.hnclothing.comment.CommentService;
import com.hnclothing.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody Map<String, Object> req, @AuthenticationPrincipal User user) {
        commentService.saveNewComment(
                (Integer) req.get("productId"), user.getId(),
                (String) req.get("content"), (Integer) req.get("rate")
        );
        return ResponseEntity.ok(Map.of("message", "Gửi đánh giá thành công"));
    }

    @PostMapping("/reply")
    public ResponseEntity<?> reply(@RequestBody Map<String, Object> req, @AuthenticationPrincipal User user) {
        commentService.replyToComment((Integer) req.get("parentId"), user.getId(), (String) req.get("content"));
        return ResponseEntity.ok(Map.of("message", "Trả lời thành công"));
    }
}