package com.cloudcost.sentinel.billing;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping
    public BillingRecord createBillingRecord(@RequestBody CreateBillingRecordRequest request) {
        return billingService.createBillingRecord(request);
    }

    @PostMapping("/upload")
    public CsvUploadResponse uploadCsv(
            @RequestParam Long userId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return billingService.uploadCsv(userId, file);
    }

    @GetMapping
    public List<BillingRecord> getBillingRecords(@RequestParam Long userId) {
        return billingService.getBillingRecords(userId);
    }

    @GetMapping("/total")
    public Map<String, BigDecimal> getTotalCost(@RequestParam Long userId) {
        BigDecimal totalCost = billingService.getTotalCost(userId);
        return Map.of("totalCost", totalCost);
    }

    @GetMapping("/cost-by-service")
    public Map<String, BigDecimal> getCostByService(@RequestParam Long userId) {
        List<Object[]> rows = billingService.getCostByService(userId);

        Map<String, BigDecimal> response = new LinkedHashMap<>();

        for (Object[] row : rows) {
            String service = (String) row[0];
            BigDecimal cost = (BigDecimal) row[1];
            response.put(service, cost);
        }

        return response;
    }

    @GetMapping("/idle")
    public List<BillingRecord> getIdleResources(@RequestParam Long userId) {
        return billingService.getIdleResources(userId);
    }

    @GetMapping("/recommendations")
    public List<OptimizationRecommendation> getOptimizationRecommendations(@RequestParam Long userId) {
        return billingService.getOptimizationRecommendations(userId);
    }
}
