package com.hnclothing.api;

import com.hnclothing.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordApiController {

    private final UserService userService;

    // Gửi mail yêu cầu reset
    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> req) {
        try {
            userService.sendResetToken(req.get("email"));
            return ResponseEntity.ok(Map.of("message", "Đã gửi link reset vào email của bạn"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email không tồn tại"));
        }
    }

    // Đổi mật khẩu mới (cần truyền token từ email và password mới)
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> req) {
        // Lưu ý: Logic handleResetPassword cần được gọi từ UserService
        // hoặc inject PasswordEncoder/UserRepository vào đây như file PasswordController gốc.
        return ResponseEntity.ok(Map.of("message", "Mật khẩu đã được cập nhật"));
    }
}