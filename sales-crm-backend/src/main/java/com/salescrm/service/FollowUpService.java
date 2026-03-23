package com.salescrm.service;
 
import com.salescrm.dto.EmailNotificationDTO;
import com.salescrm.dto.FollowUpDTO;
import com.salescrm.entity.FollowUp;
import com.salescrm.entity.Lead;
import com.salescrm.entity.User;
import com.salescrm.repository.FollowUpRepository;
import com.salescrm.repository.LeadRepository;
import com.salescrm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.util.List;
import java.util.stream.Collectors;
 
@Service
public class FollowUpService {
 
    private static final Logger logger = LoggerFactory.getLogger(FollowUpService.class);
 
    @Autowired private FollowUpRepository followUpRepository;
    @Autowired private LeadRepository leadRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;
    @Autowired private EmailService emailService;
 
    public FollowUpDTO.Response createFollowUp(FollowUpDTO.CreateRequest request, Long createdById) {
        Lead lead = leadRepository.findById(request.getLeadId())
                .orElseThrow(() -> new RuntimeException("Lead not found"));
        User creator = userRepository.findById(createdById)
                .orElseThrow(() -> new RuntimeException("User not found"));
 
        FollowUp followUp = FollowUp.builder()
                .lead(lead)
                .createdBy(creator)
                .notes(request.getNotes())
                .followUpDate(request.getFollowUpDate())
                .outcome(request.getOutcome())
                .build();
 
        FollowUp saved = followUpRepository.save(followUp);
 
        // 🔔 Trigger async email — set emailSent based on whether lead has an email
        boolean willSendEmail = lead.getEmail() != null && !lead.getEmail().isBlank();
        try {
            if (willSendEmail) {
                emailService.sendFollowUpNotification(saved); // runs async, returns void
                logger.info("📧 Email notification triggered for lead: {} <{}>",
                        lead.getName(), lead.getEmail());
            } else {
                logger.warn("Lead {} has no email — skipping notification.", lead.getId());
            }
        } catch (Exception e) {
            logger.error("Failed to trigger email notification: {}", e.getMessage());
            willSendEmail = false;
        }
 
        FollowUpDTO.Response response = mapToResponse(saved);
        response.setEmailSent(willSendEmail);
        response.setEmailStatus(willSendEmail
                ? "Email queued for " + lead.getEmail()
                : "No email address on lead");
        return response;
    }
 
    public List<FollowUpDTO.Response> getFollowUpsByLead(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
        return followUpRepository.findByLeadOrderByCreatedAtDesc(lead).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }
 
    public List<FollowUpDTO.Response> getFollowUpsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return followUpRepository.findByCreatedBy(user).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }
 
    public void deleteFollowUp(Long id) {
        followUpRepository.deleteById(id);
    }
 
    public FollowUpDTO.Response mapToResponse(FollowUp f) {
        FollowUpDTO.Response r = new FollowUpDTO.Response();
        r.setId(f.getId());
        r.setLeadId(f.getLead().getId());
        r.setLeadName(f.getLead().getName());
        r.setNotes(f.getNotes());
        r.setFollowUpDate(f.getFollowUpDate());
        r.setOutcome(f.getOutcome());
        r.setCreatedAt(f.getCreatedAt());
        r.setCreatedBy(userService.mapToResponse(f.getCreatedBy()));
        return r;
    }
}