package com.salescrm.repository;

import com.salescrm.entity.FollowUp;
import com.salescrm.entity.Lead;
import com.salescrm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUp, Long> {
    List<FollowUp> findByLead(Lead lead);
    List<FollowUp> findByCreatedBy(User user);
    List<FollowUp> findByLeadOrderByCreatedAtDesc(Lead lead);
}
