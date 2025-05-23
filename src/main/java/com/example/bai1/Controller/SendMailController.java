package com.example.bai1.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bai1.Model.ForgotPasswordRequest;
import com.example.bai1.Model.ResetPasswordRequest;
import com.example.bai1.Service.TwoFA_AccountService;

@RestController
@RequestMapping("/api/password")
public class SendMailController {
    @Autowired
    private TwoFA_AccountService TwoFA_AccountService;

    @PostMapping("/forgot")
    public String forgotPassword(@RequestBody ForgotPasswordRequest request) {
        TwoFA_AccountService.processForgotPassword(request);
        return "Link đặt lại mật khẩu đã được gửi về email (kiểm tra console để thấy link nếu chưa setup email)";
    }

    @PostMapping("/reset")
    public String resetPassword(@RequestBody ResetPasswordRequest request) {
        TwoFA_AccountService.resetPassword(request);
        return "Mật khẩu đã được thay đổi thành công.";
    }
}
