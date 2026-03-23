package com.salescrm.repository;

import com.salescrm.entity.Customer;
import com.salescrm.entity.Sale;
import com.salescrm.entity.User;
import com.salescrm.enums.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByEmployee(User employee);
    List<Sale> findByCustomer(Customer customer);
    List<Sale> findByStatus(SaleStatus status);
    List<Sale> findByEmployeeOrderByCreatedAtDesc(User employee);
    List<Sale> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.status = 'COMPLETED'")
    BigDecimal getTotalRevenue();

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.employee = :emp AND s.status = 'COMPLETED'")
    BigDecimal getRevenueByEmployee(@Param("emp") User employee);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.status = 'COMPLETED' AND s.createdAt >= :from AND s.createdAt <= :to")
    BigDecimal getRevenueBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.status = :status")
    long countByStatus(@Param("status") SaleStatus status);

    @Query("SELECT s.employee, SUM(s.totalAmount) FROM Sale s WHERE s.status = 'COMPLETED' GROUP BY s.employee ORDER BY SUM(s.totalAmount) DESC")
    List<Object[]> getTopEmployeesBySales();

    @Query("SELECT si.product, SUM(si.quantity) FROM SaleItem si GROUP BY si.product ORDER BY SUM(si.quantity) DESC")
    List<Object[]> getTopProductsBySales();
}
