package com.cloudcost.sentinel.dashboard;

import java.math.BigDecimal;
import java.util.Map;

public record DashboardSummary(
        BigDecimal totalCost,
        Map<String, BigDecimal> costByService,
        int idleResourceCount,
        int recommendationCount,
        BigDecimal estimatedMonthlySavings
) {
}
