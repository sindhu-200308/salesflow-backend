package com.salescrm.controller;

import com.salescrm.dto.*;
import com.salescrm.entity.User;
import com.salescrm.enums.LeadStatus;
import com.salescrm.repository.UserRepository;
import com.salescrm.security.UserDetailsImpl;
import com.salescrm.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired private LeadService leadService;
    @Autowired private CustomerService customerService;
    @Autowired private FollowUpService followUpService;
    @Autowired private DashboardService dashboardService;
    @Autowired private UserRepository userRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User employee = userRepository.findById(userDetails.getId()).orElseThrow();
        return ResponseEntity.ok(dashboardService.getEmployeeStats(employee));
    }

    @GetMapping("/leads")
    public ResponseEntity<List<LeadDTO.Response>> getMyLeads(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(leadService.getLeadsByEmployee(userDetails.getId()));
    }

    @GetMapping("/leads/{id}")
    public ResponseEntity<?> getLead(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.getLeadById(id));
    }

    @PutMapping("/leads/{id}/status")
    public ResponseEntity<?> updateLeadStatus(@PathVariable Long id, @RequestParam LeadStatus status) {
        return ResponseEntity.ok(leadService.updateLeadStatus(id, status));
    }

    @PostMapping("/leads/{leadId}/convert")
    public ResponseEntity<?> convertLeadToCustomer(@PathVariable Long leadId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(customerService.convertLeadToCustomer(leadId, userDetails.getId()));
    }

    @PostMapping("/followups")
    public ResponseEntity<?> addFollowUp(@Valid @RequestBody FollowUpDTO.CreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(followUpService.createFollowUp(request, userDetails.getId()));
    }

    @GetMapping("/followups/lead/{leadId}")
    public ResponseEntity<List<FollowUpDTO.Response>> getFollowUps(@PathVariable Long leadId) {
        return ResponseEntity.ok(followUpService.getFollowUpsByLead(leadId));
    }

    @GetMapping("/followups/my")
    public ResponseEntity<List<FollowUpDTO.Response>> getMyFollowUps(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(followUpService.getFollowUpsByUser(userDetails.getId()));
    }
}
