package com.denden.assignment.service.impl;

import com.denden.assignment.service.EmailService;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "app.email.provider", havingValue = "mailjet")
public class MailjetEmailService implements EmailService {

    @Value("${mailjet.api-key}")
    private String apiKey;

    @Value("${mailjet.secret-key}")
    private String secretKey;

    @Value("${mailjet.sender-email}")
    private String senderEmail;

    @Override
    @Async
    public void sendActivationEmail(String to, String activationLink) {
        sendEmail(to, "Activate your account", "Click here to activate: " + activationLink);
    }

    @Override
    @Async
    public void sendTwoFactorCode(String to, String code) {
        sendEmail(to, "Your 2FA Code", "Your login code is: " + code);
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            MailjetClient client = new MailjetClient(
                    ClientOptions.builder().apiKey(apiKey).apiSecretKey(secretKey).build());

            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", senderEmail)
                                            .put("Name", "DenDen Assignment"))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", to)))
                                    .put(Emailv31.Message.SUBJECT, subject)
                                    .put(Emailv31.Message.TEXTPART, content)
                                    .put(Emailv31.Message.HTMLPART, "<h3>" + content + "</h3>")));

            MailjetResponse response = client.post(request);
            log.info("Email sent to {}: Status {}", to, response.getStatus());
        } catch (MailjetException e) {
            log.error("Error sending email to {}", to, e);
        }
    }
}
