package com.example.bai1.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bai1.Model.Account;
import com.example.bai1.Model.ResetPasswordRequest;
import com.example.bai1.Service.PasswordResetService;

import jakarta.mail.MessagingException;

@RequestMapping("/api/auth")
@RestController
public class AuthController {
    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> processForgotPassword(@RequestBody Map<String, String> request) throws MessagingException {
        String email = request.get("email");

        try {
            passwordResetService.processForgotPassword(email);
            return ResponseEntity.ok(Map.of("message", "Đã gửi email đặt lại mật khẩu."));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        System.out.println("Token: " + request.getToken());
        System.out.println("New Password: " + request.getNewPassword());
        String token = request.getToken();
        String newPassword = request.getNewPassword();

        Account account = passwordResetService.validatePasswordResetToken(token);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Token không hợp lệ hoặc đã hết hạn."));
        }

        passwordResetService.updatePassword(account, newPassword, passwordEncoder);
        return ResponseEntity.ok(Map.of("message", "Mật khẩu đã được đặt lại thành công."));
    }
}
