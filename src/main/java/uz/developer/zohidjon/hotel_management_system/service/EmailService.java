package uz.developer.zohidjon.hotel_management_system.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendEmail(
            String to,
            String username,
            String activationCode,
            String subject
    ) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MULTIPART_MODE_MIXED, UTF_8.name());

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("activation_code", activationCode);

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom("yuldashevzohidjon252@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);

        String template = templateEngine.process("activate_account", context);
        helper.setText(template, true);

        log.debug("Sending email to: {}", to);
        mailSender.send(mimeMessage);
        log.info("Email sent successfully to: {}", to);
    }

    @Async
    public void sendPasswordResetEmail(String to, String username, String resetToken) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MULTIPART_MODE_MIXED, UTF_8.name());

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("reset_token", resetToken);

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom("noreply@yourplatform.com");
        helper.setTo(to);
        helper.setSubject("Password Reset Request");

        String template = templateEngine.process("password_reset", context);
        helper.setText(template, true);

        log.debug("Sending password reset email to: {}", to);
        mailSender.send(mimeMessage);
        log.info("Password reset email sent successfully to: {}", to);
    }
}