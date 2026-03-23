package com.salescrm.controller;

import com.salescrm.dto.*;
import com.salescrm.entity.User;
import com.salescrm.enums.LeadStatus;
import com.salescrm.enums.RoleType;
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
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private LeadService leadService;
    @Autowired private CustomerService customerService;
    @Autowired private ProductService productService;
    @Autowired private DashboardService dashboardService;
    @Autowired private UserRepository userRepository;

    // --- Dashboard ---
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminStats());
    }

    // --- Employees ---
    @PostMapping("/employees")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody UserDTO.CreateRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping("/employees")
    public ResponseEntity<List<UserDTO.Response>> getAllEmployees() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/employees/managers")
    public ResponseEntity<List<UserDTO.Response>> getManagers() {
        return ResponseEntity.ok(userService.getUsersByRole(RoleType.ROLE_SALES_MANAGER));
    }

    @GetMapping("/employees/sales")
    public ResponseEntity<List<UserDTO.Response>> getSalesEmployees() {
        return ResponseEntity.ok(userService.getUsersByRole(RoleType.ROLE_SALES_EMPLOYEE));
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<?> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody UserDTO.UpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Employee deactivated");
    }

    // --- Leads ---
    @PostMapping("/leads")
    public ResponseEntity<?> createLead(@Valid @RequestBody LeadDTO.CreateRequest request,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(leadService.createLead(request, userDetails.getId()));
    }

    @GetMapping("/leads")
    public ResponseEntity<List<LeadDTO.Response>> getAllLeads() {
        return ResponseEntity.ok(leadService.getAllLeads());
    }

    @GetMapping("/leads/{id}")
    public ResponseEntity<?> getLead(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.getLeadById(id));
    }

    @PutMapping("/leads/{id}")
    public ResponseEntity<?> updateLead(@PathVariable Long id, @RequestBody LeadDTO.UpdateRequest request) {
        return ResponseEntity.ok(leadService.updateLead(id, request));
    }

    @DeleteMapping("/leads/{id}")
    public ResponseEntity<?> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.ok("Lead deleted");
    }

    @PostMapping("/leads/{leadId}/assign-manager/{managerId}")
    public ResponseEntity<?> assignLeadToManager(@PathVariable Long leadId, @PathVariable Long managerId) {
        return ResponseEntity.ok(leadService.assignLeadToManager(leadId, managerId));
    }

    // --- Customers ---
    @PostMapping("/customers")
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CustomerDTO.CreateRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDTO.Response>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO.UpdateRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted");
    }

    // --- Products ---
    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO.CreateRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO.Response>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDTO.UpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted");
    }
}
