package com.salescrm.controller;

import com.salescrm.dto.EmailNotificationDTO;
import com.salescrm.entity.FollowUp;
import com.salescrm.repository.FollowUpRepository;
import com.salescrm.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired private EmailService emailService;
    @Autowired private FollowUpRepository followUpRepository;

    /**
     * Test SMTP configuration — Admin only
     * GET /api/email/test?to=someone@example.com
     */
    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmailNotificationDTO> testEmail(
            @RequestParam String to) {
        return ResponseEntity.ok(emailService.sendTestEmail(to));
    }

    /**
     * Manually resend email for a specific follow-up — Admin only
     * POST /api/email/resend/followup/{followUpId}
     */
    @PostMapping("/resend/followup/{followUpId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resendFollowUpEmail(@PathVariable Long followUpId) {
        FollowUp followUp = followUpRepository.findById(followUpId)
                .orElseThrow(() -> new RuntimeException("Follow-up not found: " + followUpId));

        emailService.sendFollowUpNotification(followUp);

        return ResponseEntity.ok(EmailNotificationDTO.builder()
                .success(true)
                .message("Re-send triggered for follow-up #" + followUpId +
                         " → " + followUp.getLead().getEmail())
                .toEmail(followUp.getLead().getEmail())
                .leadName(followUp.getLead().getName())
                .build());
    }
}
