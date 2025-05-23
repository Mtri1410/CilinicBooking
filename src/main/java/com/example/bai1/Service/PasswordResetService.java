package com.example.bai1.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.bai1.Model.Account;
import com.example.bai1.Repository.Doctor.AccountRepository;

import jakarta.mail.MessagingException;

@Service
public class PasswordResetService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EmailService emailService;

    public void processForgotPassword(String email) throws MessagingException {
        Account optionalAccount = accountRepository.findByEmail(email);
        if (optionalAccount == null) {
            throw new RuntimeException("Email không tồn tại.");
        }
        String token = UUID.randomUUID().toString();

        optionalAccount.setResetPasswordToken(token);
        optionalAccount.setReset_token_expiry(LocalDateTime.now().plusMinutes(30));
        accountRepository.save(optionalAccount);

        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        String subject = "Yêu cầu khôi phục mật khẩu";
        String body = "Nhấp vào liên kết sau để đặt lại mật khẩu:\n" + resetLink;

        emailService.sendRequestPasswordEmail(optionalAccount.getEmail(), subject, body);
    }

    public Account validatePasswordResetToken(String token) {
        Account optionalAccount = accountRepository.findByResetPasswordToken(token);
        if (optionalAccount == null) {
            return null;
        }

        if (optionalAccount.getReset_token_expiry() == null
                || optionalAccount.getReset_token_expiry().isBefore(LocalDateTime.now())) {
            return null;
        }

        return optionalAccount;
    }

    public void updatePassword(Account account, String newPassword, PasswordEncoder passwordEncoder) {
        account.setPassword(passwordEncoder.encode(newPassword));
        account.setResetPasswordToken(null);
        account.setReset_token_expiry(null);
        accountRepository.save(account);
    }
}
