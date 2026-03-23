package com.salescrm.service;

import com.salescrm.dto.LeadDTO;
import com.salescrm.entity.Lead;
import com.salescrm.entity.User;
import com.salescrm.enums.LeadStatus;
import com.salescrm.repository.LeadRepository;
import com.salescrm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeadService {

    @Autowired private LeadRepository leadRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;

    public LeadDTO.Response createLead(LeadDTO.CreateRequest request, Long createdById) {
        User creator = userRepository.findById(createdById)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Lead lead = Lead.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .company(request.getCompany())
                .source(request.getSource())
                .notes(request.getNotes())
                .status(LeadStatus.NEW)
                .createdBy(creator)
                .build();
        return mapToResponse(leadRepository.save(lead));
    }

    public List<LeadDTO.Response> getAllLeads() {
        return leadRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public LeadDTO.Response getLeadById(Long id) {
        return mapToResponse(leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found: " + id)));
    }

    public List<LeadDTO.Response> getLeadsByManager(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        return leadRepository.findByAssignedManager(manager).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<LeadDTO.Response> getLeadsByEmployee(Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return leadRepository.findByAssignedEmployee(employee).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public LeadDTO.Response updateLead(Long id, LeadDTO.UpdateRequest request) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found: " + id));
        if (request.getName() != null) lead.setName(request.getName());
        if (request.getEmail() != null) lead.setEmail(request.getEmail());
        if (request.getPhone() != null) lead.setPhone(request.getPhone());
        if (request.getCompany() != null) lead.setCompany(request.getCompany());
        if (request.getSource() != null) lead.setSource(request.getSource());
        if (request.getNotes() != null) lead.setNotes(request.getNotes());
        if (request.getStatus() != null) lead.setStatus(request.getStatus());
        return mapToResponse(leadRepository.save(lead));
    }

    public LeadDTO.Response assignLeadToManager(Long leadId, Long managerId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        lead.setAssignedManager(manager);
        return mapToResponse(leadRepository.save(lead));
    }

    public LeadDTO.Response assignLeadToEmployee(Long leadId, Long employeeId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        lead.setAssignedEmployee(employee);
        if (lead.getStatus() == LeadStatus.NEW) lead.setStatus(LeadStatus.CONTACTED);
        return mapToResponse(leadRepository.save(lead));
    }

    public LeadDTO.Response updateLeadStatus(Long leadId, LeadStatus status) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
        lead.setStatus(status);
        return mapToResponse(leadRepository.save(lead));
    }

    public void deleteLead(Long id) {
        leadRepository.deleteById(id);
    }

    public LeadDTO.Response mapToResponse(Lead lead) {
        LeadDTO.Response r = new LeadDTO.Response();
        r.setId(lead.getId());
        r.setName(lead.getName());
        r.setEmail(lead.getEmail());
        r.setPhone(lead.getPhone());
        r.setCompany(lead.getCompany());
        r.setSource(lead.getSource());
        r.setNotes(lead.getNotes());
        r.setStatus(lead.getStatus());
        r.setCreatedAt(lead.getCreatedAt());
        r.setUpdatedAt(lead.getUpdatedAt());
        if (lead.getAssignedManager() != null) r.setAssignedManager(userService.mapToResponse(lead.getAssignedManager()));
        if (lead.getAssignedEmployee() != null) r.setAssignedEmployee(userService.mapToResponse(lead.getAssignedEmployee()));
        if (lead.getCreatedBy() != null) r.setCreatedBy(userService.mapToResponse(lead.getCreatedBy()));
        return r;
    }
}
