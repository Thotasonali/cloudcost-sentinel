package com.cloudcost.sentinel.billing;

import com.cloudcost.sentinel.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "billing_records")
public class BillingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate usageDate;

    private String service;

    private String resourceId;

    private String region;

    @Column(precision = 12, scale = 2)
    private BigDecimal cost;

    private Double usageHours;

    private Double cpuUtilization;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User owner;

    public BillingRecord() {
    }

    public BillingRecord(
            User owner,
            LocalDate usageDate,
            String service,
            String resourceId,
            String region,
            BigDecimal cost,
            Double usageHours,
            Double cpuUtilization
    ) {
        this.owner = owner;
        this.usageDate = usageDate;
        this.service = service;
        this.resourceId = resourceId;
        this.region = region;
        this.cost = cost;
        this.usageHours = usageHours;
        this.cpuUtilization = cpuUtilization;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public LocalDate getUsageDate() {
        return usageDate;
    }

    public void setUsageDate(LocalDate usageDate) {
        this.usageDate = usageDate;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Double getUsageHours() {
        return usageHours;
    }

    public void setUsageHours(Double usageHours) {
        this.usageHours = usageHours;
    }

    public Double getCpuUtilization() {
        return cpuUtilization;
    }

    public void setCpuUtilization(Double cpuUtilization) {
        this.cpuUtilization = cpuUtilization;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
