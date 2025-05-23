package com.example.bai1.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.bai1.Model.Account;
import com.example.bai1.Model.ForgotPasswordRequest;
import com.example.bai1.Model.ResetPasswordRequest;
import com.example.bai1.Repository.Doctor.AccountRepository;

import jakarta.mail.MessagingException;

@Service
public class TwoFA_AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    public void processForgotPassword(ForgotPasswordRequest request) {
        Account account = accountRepository.findByEmail(request.getEmail());
        if (account != null) {
            String token = UUID.randomUUID().toString();
            account.setResetPasswordToken(token);
            account.setReset_token_expiry(LocalDateTime.now().plusHours(1));
            accountRepository.save(account);
            String subject = "Reset Password Request";
            String content = "<p>Để đặt lại mật khẩu của bạn, hãy nhấp vào liên kết dưới đây:</p>"
                    + "<a href='http://localhost:8080/reset-password?token=" + token + "'>Reset Password</a>";
            try {
                emailService.sendRequestPasswordEmail(account.getEmail(), subject, content);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        }
    }

    public void resetPassword(ResetPasswordRequest request) {
        Account account = accountRepository.findByResetPasswordToken(request.getToken());
        if (account != null) {
            if (account.getReset_token_expiry().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Token đã hết hạn.");
            }
            account.setPassword(passwordEncoder.encode(request.getNewPassword()));
            account.setResetPasswordToken(null);
            account.setReset_token_expiry(null);
            accountRepository.save(account);
        } else {
            throw new RuntimeException("Token không hợp lệ.");
        }
    }
}
