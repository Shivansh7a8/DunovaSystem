package com.dunova.mailform.service;

import com.dunova.mailform.model.ContactRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.mail.company-inbox}")
    private String companyInbox;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a thank-you email to the person who submitted the form,
     * and a notification email with the form details to the company inbox.
     */
    public void sendFormEmails(ContactRequest request) {
        sendThankYouEmail(request);
        sendCompanyNotificationEmail(request);
    }

    private void sendThankYouEmail(ContactRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(request.getEmail());
        message.setSubject("Thank you for reaching out, " + request.getName() + "!");
        message.setText(buildThankYouBody(request));

        try {
            mailSender.send(message);
            log.info("Thank-you email sent to {}", request.getEmail());
        } catch (Exception e) {
            log.error("Failed to send thank-you email to {}", request.getEmail(), e);
            throw e;
        }
    }

    private void sendCompanyNotificationEmail(ContactRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(companyInbox);
        message.setSubject("New contact form submission from " + request.getName());
        message.setText(buildCompanyBody(request));

        try {
            mailSender.send(message);
            log.info("Notification email sent to company inbox {}", companyInbox);
        } catch (Exception e) {
            log.error("Failed to send notification email to company inbox", e);
            throw e;
        }
    }

    private String buildThankYouBody(ContactRequest request) {
        return """
                Hi %s,

                Thank you for getting in touch with us! We have received your message and our team will get back to you as soon as possible.

                Here is a copy of what you submitted:
                Company: %s
                Message: %s

                Best regards,
                The Team
                """.formatted(
                request.getName(),
                blankIfNull(request.getCompany()),
                request.getMessage()
        );
    }

    private String buildCompanyBody(ContactRequest request) {
        return """
                A new contact form submission has been received.

                Name: %s
                Email: %s
                Company: %s
                Message: %s
                """.formatted(
                request.getName(),
                request.getEmail(),
                blankIfNull(request.getCompany()),
                request.getMessage()
        );
    }

    private String blankIfNull(String value) {
        return value == null ? "" : value;
    }
}
