package com.capricon.Notifications_Service.service;

import com.capricon.Notifications_Service.model.VerificationCode;
import com.capricon.Notifications_Service.repository.VerificationRepo;
import com.capricon.Notifications_Service.utils.CodeGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import software.xdev.brevo.api.TransactionalEmailsApi;
import software.xdev.brevo.client.ApiException;
import software.xdev.brevo.model.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final SpringTemplateEngine templateEngine;
    private final TransactionalEmailsApi emailsApi;
    private final VerificationRepo verificationRepo;

    @Value("${app.email.from.address}")
    private String fromEmail;

    @Value("${app.email.from.name}")
    private String fromName;

    @Value("${app.verification.expiry-minutes}")
    private int expiryInMinutes;

    @RabbitListener(queues = "q.email.signup")
    public void sendValidationCode(String email) {
        try {
            log.info("Message consumed and sending email...: {}", email);

            // Generate code
            String validationCode = CodeGeneratorUtil.generateVerificationCode(6);

            Map<String, Object> variables = new HashMap<>();
            variables.put("email", email);
            variables.put("verificationCode", validationCode);

            // Build VerificationCode model and save to db for validation
            VerificationCode code = new VerificationCode();
            code.setCode(validationCode);
            code.setEmail(email);
            code.setIssuedAt(LocalDateTime.now());
            code.setExpiresAt(code.getIssuedAt().plusMinutes(15)); // Expires after 15 minutes
            code.setChannel("Email");

            verificationRepo.save(code);

            buildAndSendTemplateEmail(
                    email,
                    "Account validation.",
                    "email-verification",
                    variables
            );
        } catch (Exception ex) {
            log.error("Error sending email, caused by: {}", ex.getMessage());
        }

    }

    public void buildAndSendTemplateEmail(String toEmail, String subject, String templateName, Map<String, Object> variables) {
        // Implementation for sending email using Brevo API
        try {
            // Create thymeleaf context and process template
            Context context = new Context();
            variables.forEach(context::setVariable);
            String htmlContent = templateEngine.process("emails/" + templateName, context);

            Map<String, Object> headers = new HashMap<>();
            headers.put("X-Mailer", "Brevo-Java-Client");

            Map<String, Object> params = new HashMap<>();
            params.put("email", toEmail);
            params.put("verificationCode", variables.get("verificationCode"));

            // Set up the sender
            SendSmtpEmail emailRequest = getSendSmtpEmail(toEmail, subject, htmlContent, headers, params);

            // Send the email via brevo api
            CreateSmtpEmail response = emailsApi.sendTransacEmail(emailRequest);
            log.info("Email sent with message ID: {}", response.getMessageId());

        } catch (ApiException ex) {
            log.error("Error sending email via brevo, caused by: {}", ex.getMessage());
        }

    }

    private SendSmtpEmail getSendSmtpEmail(String toEmail, String subject,
                                           String htmlContent, Map<String, Object> headers, Map<String, Object> params) {
        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(fromEmail);
        sender.setName(fromName);

        // Set up the recipient; the receiver of the email
        List<SendSmtpEmailToInner> recipients = new ArrayList<>();
        SendSmtpEmailToInner recipient = new SendSmtpEmailToInner();
        recipient.setEmail(toEmail);
        recipients.add(recipient);

        // Create empty cc and bcc lists
//        List<SendSmtpEmailCcInner> ccList = new ArrayList<>();
//        List<SendSmtpEmailBccInner> bccList = new ArrayList<>();

        // Create email request
        SendSmtpEmail emailRequest = new SendSmtpEmail();
        emailRequest.setSender(sender);
        emailRequest.setSubject(subject);
        emailRequest.setTo(recipients);
        emailRequest.setHtmlContent(htmlContent);
        emailRequest.setCc(null);
        emailRequest.setBcc(null);
        emailRequest.setAttachment(null);
        emailRequest.setHeaders(headers);
        emailRequest.setParams(params);
        emailRequest.setTags(null);

        // Add message versions
        List<SendSmtpEmailMessageVersionsInner> messageVersionsInnerList = new ArrayList<>();
        SendSmtpEmailMessageVersionsInner messageVersionsInner = new SendSmtpEmailMessageVersionsInner();
        messageVersionsInner.setTo(recipients);
        messageVersionsInner.setSubject(subject);
        messageVersionsInner.setCc(null);
        messageVersionsInner.setBcc(null);
        messageVersionsInnerList.add(messageVersionsInner);

        emailRequest.setMessageVersions(messageVersionsInnerList);

        return emailRequest;
    }


}
