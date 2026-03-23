package com.salescrm.controller;

import com.salescrm.dto.*;
import com.salescrm.entity.User;
import com.salescrm.enums.LeadStatus;
import com.salescrm.enums.RoleType;
import com.salescrm.repository.UserRepository;
import com.salescrm.security.UserDetailsImpl;
import com.salescrm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    @Autowired private LeadService leadService;
    @Autowired private UserService userService;
    @Autowired private DashboardService dashboardService;
    @Autowired private FollowUpService followUpService;
    @Autowired private UserRepository userRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User manager = userRepository.findById(userDetails.getId()).orElseThrow();
        return ResponseEntity.ok(dashboardService.getManagerStats(manager));
    }

    @GetMapping("/leads")
    public ResponseEntity<List<LeadDTO.Response>> getAssignedLeads(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(leadService.getLeadsByManager(userDetails.getId()));
    }

    @GetMapping("/leads/{id}")
    public ResponseEntity<?> getLead(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.getLeadById(id));
    }

    @PutMapping("/leads/{id}/status")
    public ResponseEntity<?> updateLeadStatus(@PathVariable Long id, @RequestParam LeadStatus status) {
        return ResponseEntity.ok(leadService.updateLeadStatus(id, status));
    }

    @PostMapping("/leads/{leadId}/assign-employee/{employeeId}")
    public ResponseEntity<?> assignLeadToEmployee(@PathVariable Long leadId, @PathVariable Long employeeId) {
        return ResponseEntity.ok(leadService.assignLeadToEmployee(leadId, employeeId));
    }

    @GetMapping("/employees")
    public ResponseEntity<List<UserDTO.Response>> getSalesEmployees() {
        return ResponseEntity.ok(userService.getUsersByRole(RoleType.ROLE_SALES_EMPLOYEE));
    }

    @GetMapping("/employees/{id}/leads")
    public ResponseEntity<List<LeadDTO.Response>> getEmployeeLeads(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.getLeadsByEmployee(id));
    }

    @GetMapping("/followups/lead/{leadId}")
    public ResponseEntity<List<FollowUpDTO.Response>> getFollowUps(@PathVariable Long leadId) {
        return ResponseEntity.ok(followUpService.getFollowUpsByLead(leadId));
    }
}
