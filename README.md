# CloudCost Sentinel

CloudCost Sentinel is a backend cloud cost monitoring and optimization platform built with Java Spring Boot, PostgreSQL, Docker, Maven, and REST APIs.

The project helps users track cloud billing records, upload billing data from CSV files, analyze spending by cloud service, detect idle resources, generate optimization recommendations, and view a dashboard summary with estimated savings.

---

## Features

### Billing Management

- Create billing records manually through REST APIs
- Store usage date, cloud service, resource ID, region, cost, usage hours, and CPU utilization
- Retrieve all billing records for a specific user

### CSV Billing Upload

- Upload multiple cloud billing records from a CSV file
- Validate billing data before saving
- Save valid records into PostgreSQL
- Return upload summary with total rows, saved rows, skipped rows, and errors

### Cost Analytics

- Calculate total cloud spend for a user
- Group cloud spending by service, such as EC2, RDS, S3, and Lambda
- Identify idle resources using CPU utilization and usage hours

### Optimization Recommendations

- Generate cost-saving recommendations for idle resources
- Estimate possible savings for each resource
- Provide service-specific recommendations for EC2, RDS, S3, and Lambda

### Dashboard Summary API

- Return total cost, cost by service, idle resource count, recommendation count, and estimated monthly savings in one API response

---

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Docker Compose
- Maven
- REST APIs

---

## Project Structure

```text
src/main/java/com/cloudcost/sentinel
├── billing
│   ├── BillingController.java
│   ├── BillingService.java
│   ├── BillingRepository.java
│   ├── BillingRecord.java
│   ├── CreateBillingRecordRequest.java
│   ├── CsvUploadResponse.java
│   └── OptimizationRecommendation.java
├── dashboard
│   ├── DashboardController.java
│   └── DashboardSummary.java
├── exception
│   └── GlobalExceptionHandler.java
└── user
    ├── User.java
    ├── UserRepository.java
    ├── CreateUserRequest.java
    ├── DevUserController.java
    └── Role.java
```

---

## How to Run Locally

### 1. Start PostgreSQL with Docker

```bash
docker compose up -d
```

### 2. Start the Spring Boot application

```bash
mvn spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

---

## Health Check

```bash
curl http://localhost:8080/actuator/health
```

Expected response:

```json
{"status":"UP"}
```

---

## API Examples

### Create a User

```bash
curl -X POST http://localhost:8080/api/dev/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Sonali","email":"sonali@example.com","password":"test123"}'
```

---

### Create a Billing Record

```bash
curl -X POST http://localhost:8080/api/billing \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "usageDate": "2026-07-01",
    "service": "EC2",
    "resourceId": "i-12345",
    "region": "us-east-1",
    "cost": 12.50,
    "usageHours": 24,
    "cpuUtilization": 3.2
  }'
```

---

### Upload CSV Billing Data

```bash
curl -X POST "http://localhost:8080/api/billing/upload?userId=1" \
  -F "file=@sample-billing.csv"
```

Example response:

```json
{
  "totalRows": 5,
  "savedRows": 5,
  "skippedRows": 0,
  "errors": []
}
```

---

### Get Total Cost

```bash
curl "http://localhost:8080/api/billing/total?userId=1"
```

Example response:

```json
{"totalCost":86.90}
```

---

### Get Cost by Service

```bash
curl "http://localhost:8080/api/billing/cost-by-service?userId=1"
```

Example response:

```json
{
  "EC2": 43.75,
  "RDS": 35.80,
  "S3": 5.25,
  "Lambda": 2.10
}
```

---

### Get Idle Resources

```bash
curl "http://localhost:8080/api/billing/idle?userId=1"
```

---

### Get Optimization Recommendations

```bash
curl "http://localhost:8080/api/billing/recommendations?userId=1"
```

Example response:

```json
[
  {
    "billingRecordId": 3,
    "service": "RDS",
    "resourceId": "db-prod-1",
    "region": "us-east-1",
    "currentCost": 35.80,
    "estimatedSavings": 25.06,
    "reason": "CPU utilization is below 5% and usage hours are at least 24.",
    "recommendation": "Downsize this RDS database or stop it during non-business hours."
  }
]
```

---

### Get Dashboard Summary

```bash
curl "http://localhost:8080/api/dashboard/summary?userId=1"
```

Example response:

```json
{
  "totalCost": 86.90,
  "costByService": {
    "EC2": 43.75,
    "RDS": 35.80,
    "S3": 5.25,
    "Lambda": 2.10
  },
  "idleResourceCount": 5,
  "recommendationCount": 5,
  "estimatedMonthlySavings": 59.37
}
```

---

## Sample CSV Format

```csv
usageDate,service,resourceId,region,cost,usageHours,cpuUtilization
2026-07-01,EC2,i-12345,us-east-1,12.50,24,3.2
2026-07-01,RDS,db-prod-1,us-east-1,35.80,24,4.1
2026-07-02,S3,bucket-logs,us-east-1,5.25,24,0.0
2026-07-02,Lambda,function-image-processor,us-east-1,2.10,3,12.5
2026-07-03,EC2,i-67890,us-west-2,18.75,48,2.1
```

---

## Completed Phases

| Phase | Feature |
|---|---|
| Phase 1 | Billing APIs and PostgreSQL integration |
| Phase 2 | CSV billing upload |
| Phase 3 | Optimization recommendations |
| Phase 4 | Dashboard summary API |

---

## Future Improvements

- Add JWT authentication
- Add a frontend dashboard using React
- Add budget alert APIs
- Add AWS Cost Explorer integration
- Add charts for service-level spending
- Add duplicate billing record detection
- Add monthly trend analytics
- Add role-based access control

---

## Author

Sonali Thota
