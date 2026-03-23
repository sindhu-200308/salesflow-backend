package com.salescrm.service;
 
import com.salescrm.dto.EmailNotificationDTO;
import com.salescrm.entity.FollowUp;
import com.salescrm.entity.Lead;
import com.salescrm.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
 
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
 
@Service
public class EmailService {
 
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
 
    @Autowired
    private JavaMailSender mailSender;
 
    @Autowired
    private TemplateEngine templateEngine;
 
    @Value("${app.mail.from}")
    private String fromEmail;
 
    @Value("${app.mail.from-name}")
    private String fromName;
 
    @Value("${app.mail.enabled:true}")
    private boolean emailEnabled;
 
    // ─────────────────────────────────────────────────────────────
    //  Main method: send follow-up notification to lead
    // ─────────────────────────────────────────────────────────────
 
    @Async
    public void sendFollowUpNotification(FollowUp followUp) {
        Lead lead = followUp.getLead();
        User employee = followUp.getCreatedBy();
 
        if (!emailEnabled) {
            logger.warn("Email is disabled — skipping notification for lead: {}", lead.getEmail());
            return;
        }
 
        if (lead.getEmail() == null || lead.getEmail().isBlank()) {
            logger.warn("Lead {} has no email address — skipping.", lead.getId());
            return;
        }
 
        try {
            Context ctx = new Context();
            ctx.setVariable("leadName",      lead.getName());
            ctx.setVariable("leadCompany",   lead.getCompany());
            ctx.setVariable("leadStatus",    lead.getStatus().name());
            ctx.setVariable("followUpNotes", followUp.getNotes());
            ctx.setVariable("outcome",       followUp.getOutcome());
            ctx.setVariable("followUpTimestamp",
                followUp.getCreatedAt() != null
                    ? followUp.getCreatedAt().format(DISPLAY_FORMAT)
                    : "Just now");
            ctx.setVariable("nextFollowUpDate",
                followUp.getFollowUpDate() != null
                    ? followUp.getFollowUpDate().format(DISPLAY_FORMAT)
                    : null);
            ctx.setVariable("employeeName",     employee.getName());
            ctx.setVariable("employeeEmail",    employee.getEmail());
            ctx.setVariable("employeeInitials", getInitials(employee.getName()));
 
            String htmlBody = templateEngine.process("follow-up-email", ctx);
            String subject  = "Follow-Up Update: " + lead.getName() +
                              " [" + lead.getStatus().name() + "]";
 
            sendHtmlEmail(lead.getEmail(), lead.getName(), subject, htmlBody);
 
            logger.info("✅ Follow-up email sent → {} ({})", lead.getName(), lead.getEmail());
 
        } catch (MessagingException | MailException | UnsupportedEncodingException e) {
            logger.error("❌ Failed to send email to {}: {}", lead.getEmail(), e.getMessage());
        }
    }
 
    // ─────────────────────────────────────────────────────────────
    //  Send a test email (for verifying SMTP config)
    // ─────────────────────────────────────────────────────────────
 
    public EmailNotificationDTO sendTestEmail(String toEmail) {
        try {
            String subject = "SalesCRM — SMTP Test Email";
            String body = """
                    <html><body style="font-family:Arial,sans-serif; padding:30px;">
                      <h2 style="color:#f59e0b;">💼 SalesCRM</h2>
                      <p>Your email configuration is working correctly!</p>
                      <p style="color:#64748b; font-size:13px;">
                        This is a test email from SalesCRM.<br/>
                        If you received this, your SMTP settings are correctly configured.
                      </p>
                    </body></html>
                    """;
            sendHtmlEmail(toEmail, "Admin", subject, body);
            return EmailNotificationDTO.builder()
                    .success(true).message("Test email sent to " + toEmail).build();
        } catch (MessagingException | MailException | UnsupportedEncodingException e) {
            return EmailNotificationDTO.builder()
                    .success(false).message("Failed: " + e.getMessage()).build();
        }
    }
 
    // ─────────────────────────────────────────────────────────────
    //  Private helpers
    // ─────────────────────────────────────────────────────────────
 
    private void sendHtmlEmail(String toEmail, String toName,
                               String subject, String htmlBody)
            throws MessagingException, UnsupportedEncodingException {
 
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
 
        try {
            helper.setFrom(fromEmail, fromName);
        } catch (UnsupportedEncodingException e) {
            // Fallback: set from without display name
            helper.setFrom(fromEmail);
        }
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true = HTML
 
        mailSender.send(message);
    }
 
    private String getInitials(String name) {
        if (name == null || name.isBlank()) return "??";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
    }
 
    private EmailNotificationDTO buildResult(boolean success, String message,
                                             Lead lead, User employee) {
        return EmailNotificationDTO.builder()
                .success(success)
                .message(message)
                .toEmail(lead.getEmail())
                .leadName(lead.getName())
                .employeeName(employee.getName())
                .build();
    }
}