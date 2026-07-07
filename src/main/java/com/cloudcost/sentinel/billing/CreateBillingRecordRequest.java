package com.cloudcost.sentinel.billing;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateBillingRecordRequest {

    private Long userId;
    private LocalDate usageDate;
    private String service;
    private String resourceId;
    private String region;
    private BigDecimal cost;
    private Double usageHours;
    private Double cpuUtilization;

    public CreateBillingRecordRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDate getUsageDate() {
        return usageDate;
    }

    public String getService() {
        return service;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getRegion() {
        return region;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public Double getUsageHours() {
        return usageHours;
    }

    public Double getCpuUtilization() {
        return cpuUtilization;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsageDate(LocalDate usageDate) {
        this.usageDate = usageDate;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setUsageHours(Double usageHours) {
        this.usageHours = usageHours;
    }

    public void setCpuUtilization(Double cpuUtilization) {
        this.cpuUtilization = cpuUtilization;
    }
}
