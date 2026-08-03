package com.staj.stock.controller.impl;

import com.staj.stock.controller.IAdminMailController;
import com.staj.stock.service.MailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailController implements IAdminMailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public String sendEmail(
            String to,
            String subject,
            String body) {
        try {
            mailService.sendEmail(to, subject, body);
            return "Email sent successfully!";
        } catch (Exception e) {
            return "Failed to send email: " + e.getMessage();
        }
    }

}

