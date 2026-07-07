package com.cloudcost.sentinel.billing;

import com.cloudcost.sentinel.user.User;
import com.cloudcost.sentinel.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BillingService {

    private final BillingRepository billingRepository;
    private final UserRepository userRepository;

    public BillingService(BillingRepository billingRepository, UserRepository userRepository) {
        this.billingRepository = billingRepository;
        this.userRepository = userRepository;
    }

    public BillingRecord createBillingRecord(CreateBillingRecordRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        BillingRecord record = new BillingRecord(
                user,
                request.getUsageDate(),
                request.getService(),
                request.getResourceId(),
                request.getRegion(),
                request.getCost(),
                request.getUsageHours(),
                request.getCpuUtilization()
        );

        return billingRepository.save(record);
    }

    public CsvUploadResponse uploadCsv(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (file.isEmpty()) {
            throw new RuntimeException("CSV file is empty");
        }

        int totalRows = 0;
        int savedRows = 0;
        int skippedRows = 0;

        List<String> errors = new ArrayList<>();
        List<BillingRecord> recordsToSave = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)
        )) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                if (lineNumber == 1 && line.toLowerCase().startsWith("usagedate")) {
                    continue;
                }

                totalRows++;

                try {
                    String[] columns = line.split(",", -1);

                    if (columns.length < 7) {
                        throw new RuntimeException("Expected 7 columns but found " + columns.length);
                    }

                    BillingRecord record = new BillingRecord(
                            user,
                            LocalDate.parse(columns[0].trim()),
                            columns[1].trim(),
                            columns[2].trim(),
                            columns[3].trim(),
                            new BigDecimal(columns[4].trim()),
                            Double.parseDouble(columns[5].trim()),
                            Double.parseDouble(columns[6].trim())
                    );

                    recordsToSave.add(record);
                    savedRows++;

                } catch (Exception ex) {
                    skippedRows++;
                    errors.add("Line " + lineNumber + ": " + ex.getMessage());
                }
            }
        }

        billingRepository.saveAll(recordsToSave);

        return new CsvUploadResponse(totalRows, savedRows, skippedRows, errors);
    }

    public List<BillingRecord> getBillingRecords(Long userId) {
        return billingRepository.findByOwnerId(userId);
    }

    public BigDecimal getTotalCost(Long userId) {
        BigDecimal total = billingRepository.getTotalCostByUserId(userId);
        return total == null ? BigDecimal.ZERO : total;
    }

    public List<Object[]> getCostByService(Long userId) {
        return billingRepository.getCostByService(userId);
    }

    public List<BillingRecord> getIdleResources(Long userId) {
        return billingRepository.findIdleResources(userId);
    }

    public List<OptimizationRecommendation> getOptimizationRecommendations(Long userId) {
        List<BillingRecord> idleResources = getIdleResources(userId);
        List<OptimizationRecommendation> recommendations = new ArrayList<>();

        for (BillingRecord record : idleResources) {
            String service = record.getService() == null ? "UNKNOWN" : record.getService();
            BigDecimal currentCost = record.getCost() == null ? BigDecimal.ZERO : record.getCost();

            BigDecimal estimatedSavings = currentCost
                    .multiply(new BigDecimal("0.70"))
                    .setScale(2, RoundingMode.HALF_UP);

            OptimizationRecommendation recommendation = new OptimizationRecommendation(
                    record.getId(),
                    service,
                    record.getResourceId(),
                    record.getRegion(),
                    currentCost,
                    estimatedSavings,
                    "CPU utilization is below 5% and usage hours are at least 24.",
                    buildRecommendation(service)
            );

            recommendations.add(recommendation);
        }

        return recommendations;
    }

    private String buildRecommendation(String service) {
        String normalizedService = service.toUpperCase();

        return switch (normalizedService) {
            case "EC2" -> "Stop, resize, or move this EC2 instance to a smaller instance type.";
            case "RDS" -> "Downsize this RDS database or stop it during non-business hours.";
            case "S3" -> "Move old or rarely accessed objects to cheaper storage such as Glacier.";
            case "LAMBDA" -> "Review memory allocation and invocation frequency.";
            default -> "Review this resource for rightsizing or shutdown.";
        };
    }
}
