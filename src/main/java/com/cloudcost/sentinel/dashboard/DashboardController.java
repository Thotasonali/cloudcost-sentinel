package com.cloudcost.sentinel.dashboard;

import com.cloudcost.sentinel.billing.BillingRecord;
import com.cloudcost.sentinel.billing.BillingService;
import com.cloudcost.sentinel.billing.OptimizationRecommendation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final BillingService billingService;

    public DashboardController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/summary")
    public DashboardSummary getSummary(@RequestParam Long userId) {
        BigDecimal totalCost = billingService.getTotalCost(userId);

        Map<String, BigDecimal> costByService = new LinkedHashMap<>();

        List<Object[]> costRows = billingService.getCostByService(userId);
        for (Object[] row : costRows) {
            String service = (String) row[0];
            BigDecimal cost = (BigDecimal) row[1];
            costByService.put(service, cost);
        }

        List<BillingRecord> idleResources = billingService.getIdleResources(userId);
        List<OptimizationRecommendation> recommendations =
                billingService.getOptimizationRecommendations(userId);

        BigDecimal estimatedMonthlySavings = recommendations.stream()
                .map(OptimizationRecommendation::estimatedSavings)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardSummary(
                totalCost,
                costByService,
                idleResources.size(),
                recommendations.size(),
                estimatedMonthlySavings
        );
    }
}
