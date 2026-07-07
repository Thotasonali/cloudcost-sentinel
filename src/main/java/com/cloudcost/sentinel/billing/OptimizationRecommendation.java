package com.cloudcost.sentinel.billing;

import java.math.BigDecimal;

public record OptimizationRecommendation(
        Long billingRecordId,
        String service,
        String resourceId,
        String region,
        BigDecimal currentCost,
        BigDecimal estimatedSavings,
        String reason,
        String recommendation
) {
}
