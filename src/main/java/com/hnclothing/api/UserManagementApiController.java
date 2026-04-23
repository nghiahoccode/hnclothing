package com.hnclothing.api;

import com.hnclothing.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManagementApiController {

    private final UserService userService;

    // Đăng ký tài khoản mới
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok(Map.of("message", "Đăng ký thành công"));
    }

    // Admin lấy danh sách user
    @GetMapping("/admin/list")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Admin cập nhật user
    @PostMapping("/admin/update")
    public ResponseEntity<?> adminUpdate(@RequestBody Map<String, Object> req) {
        User user = User.builder()
                .id((Integer) req.get("id"))
                .fullName((String) req.get("fullName"))
                .email((String) req.get("email"))
                .status((Boolean) req.get("enabled") ? 1 : 0)
                .build();
        userService.saveUserByAdmin(user, (String) req.get("role"));
        return ResponseEntity.ok(Map.of("message", "Cập nhật user thành công"));
    }

    // User cập nhật profile của chính mình
    @PostMapping("/profile/update")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateDTO dto, Principal principal) {
        userService.updateProfile(principal.getName(), dto);
        return ResponseEntity.ok(Map.of("message", "Cập nhật hồ sơ thành công"));
    }
}