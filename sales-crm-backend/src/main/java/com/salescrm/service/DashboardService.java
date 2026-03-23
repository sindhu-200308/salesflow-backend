package com.salescrm.service;
 
import com.salescrm.dto.DashboardDTO;
import com.salescrm.entity.User;
import com.salescrm.enums.LeadStatus;
import com.salescrm.enums.RoleType;
import com.salescrm.enums.SaleStatus;
import com.salescrm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.math.BigDecimal;
 
@Service
public class DashboardService {
 
    @Autowired private LeadRepository leadRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private FollowUpRepository followUpRepository;
    @Autowired private SaleRepository saleRepository;
 
    public DashboardDTO.AdminStats getAdminStats() {
        BigDecimal revenue = saleRepository.getTotalRevenue();
        return DashboardDTO.AdminStats.builder()
                .totalLeads(leadRepository.count())
                .newLeads(leadRepository.countByStatus(LeadStatus.NEW))
                .contactedLeads(leadRepository.countByStatus(LeadStatus.CONTACTED))
                .interestedLeads(leadRepository.countByStatus(LeadStatus.INTERESTED))
                .convertedLeads(leadRepository.countByStatus(LeadStatus.CONVERTED))
                .rejectedLeads(leadRepository.countByStatus(LeadStatus.REJECTED))
                .totalCustomers(customerRepository.count())
                .totalProducts(productRepository.count())
                .totalEmployees(userRepository.findByRole(RoleType.ROLE_SALES_EMPLOYEE).size())
                .totalManagers(userRepository.findByRole(RoleType.ROLE_SALES_MANAGER).size())
                .totalSales(saleRepository.count())
                .totalRevenue(revenue != null ? revenue : BigDecimal.ZERO)
                .build();
    }
 
    public DashboardDTO.ManagerStats getManagerStats(User manager) {
        long assigned = leadRepository.countByManager(manager);
        return DashboardDTO.ManagerStats.builder()
                .assignedLeads(assigned)
                .newLeads(leadRepository.countByManagerAndStatus(manager, LeadStatus.NEW))
                .contactedLeads(leadRepository.countByManagerAndStatus(manager, LeadStatus.CONTACTED))
                .interestedLeads(leadRepository.countByManagerAndStatus(manager, LeadStatus.INTERESTED))
                .convertedLeads(leadRepository.countByManagerAndStatus(manager, LeadStatus.CONVERTED))
                .rejectedLeads(leadRepository.countByManagerAndStatus(manager, LeadStatus.REJECTED))
                .totalEmployees(userRepository.findByRole(RoleType.ROLE_SALES_EMPLOYEE).size())
                .totalSales(saleRepository.countByStatus(SaleStatus.COMPLETED))
                .build();
    }
 
    public DashboardDTO.EmployeeStats getEmployeeStats(User employee) {
        BigDecimal revenue = saleRepository.getRevenueByEmployee(employee);
        return DashboardDTO.EmployeeStats.builder()
                .assignedLeads(leadRepository.findByAssignedEmployee(employee).size())
                .newLeads(leadRepository.countByEmployeeAndStatus(employee, LeadStatus.NEW))
                .contactedLeads(leadRepository.countByEmployeeAndStatus(employee, LeadStatus.CONTACTED))
                .interestedLeads(leadRepository.countByEmployeeAndStatus(employee, LeadStatus.INTERESTED))
                .convertedLeads(leadRepository.countByEmployeeAndStatus(employee, LeadStatus.CONVERTED))
                .rejectedLeads(leadRepository.countByEmployeeAndStatus(employee, LeadStatus.REJECTED))
                .totalFollowUps(followUpRepository.findByCreatedBy(employee).size())
                .totalSales(saleRepository.findByEmployee(employee).size())
                .myRevenue(revenue != null ? revenue : BigDecimal.ZERO)
                .build();
    }
}
 