package com.example.bai1.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendRequestPasswordEmail(String toEmail, String subject, String body) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setTo(toEmail);
        messageHelper.setSubject(subject);
        messageHelper.setText(body, true); // Set content as HTML (true)

        // Gửi email
        javaMailSender.send(mimeMessage);
    }
}
