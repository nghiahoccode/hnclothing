package com.hnclothing.controller;

import com.hnclothing.user.User;
import com.hnclothing.user.UserRepository;
import com.hnclothing.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class PasswordController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "user/forgot-password"; // Trỏ đúng tên file html bạn vừa tạo
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        try {
            userService.sendResetToken(email); // Hàm đã viết ở bước trước
            model.addAttribute("message", "Chúng tôi đã gửi một liên kết đến email của bạn.");
        } catch (Exception e) {
            model.addAttribute("error", "Không tìm thấy tài khoản với email này.");
        }
        return "user/forgot-password";
    }

    @GetMapping("/reset-password") // Spring sẽ hứng link có dạng /reset-password?token=...
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {


        User user = userRepository.findByResetToken(token)
                .orElse(null);

        // 2. Kiểm tra token hợp lệ và chưa hết hạn
        if (user == null || user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Liên kết đã hết hạn hoặc không hợp lệ.");
            return "user/forgot-password";
        }

        // 3. Nếu OK, gửi token sang trang đổi mật khẩu
        model.addAttribute("token", token);
        return "user/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("token") String token,
                                      @RequestParam("password") String newPassword,
                                      Model model) {
        // 1. Tìm user theo token một lần nữa để bảo mật
        User user = userRepository.findByResetToken(token).orElse(null);

        if (user == null || user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            return "user/forgot-password";
        }

        // 2. Mã hóa và cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));

        // 3. Xóa token sau khi dùng xong để bảo mật
        user.setResetToken(null);
        user.setTokenExpiration(null);

        userRepository.save(user);

        // 4. Chuyển hướng về trang login với thông báo thành công
        return "redirect:/login?reset_success";
    }


}