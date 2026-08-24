package com.staj.stock.controller.impl;

import com.staj.stock.controller.IAdminMailController;
import com.staj.stock.service.MailService;
import com.staj.stock.util.Translator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
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
            return Translator.toLocale("mail.sent.success");
        } catch (Exception e) {
            return Translator.toLocale("mail.sent.failure", e.getMessage());
        }
    }

}

