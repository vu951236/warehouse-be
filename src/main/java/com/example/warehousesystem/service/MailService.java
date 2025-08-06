package com.example.warehousesystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendForgotPasswordEmail(String toEmail, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Khôi phục mật khẩu - Warehouse System");
        message.setText("Mật khẩu mới của bạn là: " + newPassword + "\n"
                + "Vui lòng đăng nhập và đổi mật khẩu ngay để bảo mật.");

        mailSender.send(message);
    }
}
