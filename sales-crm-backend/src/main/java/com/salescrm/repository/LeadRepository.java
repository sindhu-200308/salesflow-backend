package com.salescrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.salescrm.entity.Lead;
import com.salescrm.entity.User;
import com.salescrm.enums.LeadStatus;

import java.util.List;
 
@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findByAssignedManager(User manager);
    List<Lead> findByAssignedEmployee(User employee);
    List<Lead> findByStatus(LeadStatus status);
    List<Lead> findByAssignedManagerAndStatus(User manager, LeadStatus status);
    List<Lead> findByAssignedEmployeeAndStatus(User employee, LeadStatus status);
 
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.assignedManager = :manager AND l.status = :status")
    long countByManagerAndStatus(@Param("manager") User manager, @Param("status") LeadStatus status);
 
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.status = :status")
    long countByStatus(LeadStatus status);
 
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.assignedEmployee = :employee AND l.status = :status")
    long countByEmployeeAndStatus(User employee, LeadStatus status);
 
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.assignedManager = :manager")
    long countByManager(User manager);
}