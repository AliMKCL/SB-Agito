package com.staj.stock.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

@Service
@AllArgsConstructor
public class MailService {

    private final Gmail gmailService;

    public void sendEmail(String recipientEmail, String subject, String bodyText)
            throws MessagingException, IOException {

        // 1. Build standard MimeMessage
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress("me")); // "me" represents the owner of the authenticated OAuth user (me).
        email.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipientEmail));
        email.setSubject(subject);
        email.setText(bodyText);

        // 2. Encode to Base64 URL safe string
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);

        // 3. Construct API Message payload and send
        Message message = new Message();
        message.setRaw(encodedEmail);

        gmailService.users().messages().send("me", message).execute();
    }
}
