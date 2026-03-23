package com.salescrm.repository;

import com.salescrm.entity.Customer;
import com.salescrm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByAssignedTo(User user);
    boolean existsByEmail(String email);
}
