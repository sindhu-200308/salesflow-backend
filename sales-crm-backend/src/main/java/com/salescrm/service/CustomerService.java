package com.salescrm.service;

import com.salescrm.dto.CustomerDTO;
import com.salescrm.entity.Customer;
import com.salescrm.entity.Lead;
import com.salescrm.entity.User;
import com.salescrm.enums.LeadStatus;
import com.salescrm.repository.CustomerRepository;
import com.salescrm.repository.LeadRepository;
import com.salescrm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired private CustomerRepository customerRepository;
    @Autowired private LeadRepository leadRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;

    public CustomerDTO.Response createCustomer(CustomerDTO.CreateRequest request) {
        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .company(request.getCompany())
                .address(request.getAddress())
                .build();
        return mapToResponse(customerRepository.save(customer));
    }

    public CustomerDTO.Response convertLeadToCustomer(Long leadId, Long employeeId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (customerRepository.existsByEmail(lead.getEmail()))
            throw new RuntimeException("Customer already exists with email: " + lead.getEmail());

        Customer customer = Customer.builder()
                .name(lead.getName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .company(lead.getCompany())
                .convertedFromLead(lead)
                .assignedTo(employee)
                .build();

        lead.setStatus(LeadStatus.CONVERTED);
        leadRepository.save(lead);

        return mapToResponse(customerRepository.save(customer));
    }

    public List<CustomerDTO.Response> getAllCustomers() {
        return customerRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public CustomerDTO.Response getCustomerById(Long id) {
        return mapToResponse(customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id)));
    }

    public CustomerDTO.Response updateCustomer(Long id, CustomerDTO.UpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));
        if (request.getName() != null) customer.setName(request.getName());
        if (request.getPhone() != null) customer.setPhone(request.getPhone());
        if (request.getCompany() != null) customer.setCompany(request.getCompany());
        if (request.getAddress() != null) customer.setAddress(request.getAddress());
        return mapToResponse(customerRepository.save(customer));
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public CustomerDTO.Response mapToResponse(Customer c) {
        CustomerDTO.Response r = new CustomerDTO.Response();
        r.setId(c.getId());
        r.setName(c.getName());
        r.setEmail(c.getEmail());
        r.setPhone(c.getPhone());
        r.setCompany(c.getCompany());
        r.setAddress(c.getAddress());
        r.setCreatedAt(c.getCreatedAt());
        if (c.getConvertedFromLead() != null) r.setConvertedFromLeadId(c.getConvertedFromLead().getId());
        if (c.getAssignedTo() != null) r.setAssignedTo(userService.mapToResponse(c.getAssignedTo()));
        return r;
    }
}
