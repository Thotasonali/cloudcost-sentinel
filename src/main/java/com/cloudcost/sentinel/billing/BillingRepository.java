package com.cloudcost.sentinel.billing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface BillingRepository extends JpaRepository<BillingRecord, Long> {

    List<BillingRecord> findByOwnerId(Long ownerId);

    @Query("""
           SELECT COALESCE(SUM(b.cost), 0)
           FROM BillingRecord b
           WHERE b.owner.id = :userId
           """)
    BigDecimal getTotalCostByUserId(Long userId);

    @Query("""
           SELECT b.service, SUM(b.cost)
           FROM BillingRecord b
           WHERE b.owner.id = :userId
           GROUP BY b.service
           ORDER BY SUM(b.cost) DESC
           """)
    List<Object[]> getCostByService(Long userId);

    @Query("""
           SELECT b
           FROM BillingRecord b
           WHERE b.owner.id = :userId
             AND b.cpuUtilization < 5
             AND b.usageHours >= 24
           ORDER BY b.cost DESC
           """)
    List<BillingRecord> findIdleResources(Long userId);
}
