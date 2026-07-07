# Engineering Notes

This document summarizes the main technical challenges, design decisions, and implementation improvements made while building CloudCost Sentinel.

CloudCost Sentinel was developed as a phased backend project for cloud cost monitoring, billing analysis, CSV ingestion, idle resource detection, optimization recommendations, and dashboard aggregation.

---

## 1. Replacing Lombok with Explicit Java Code

### Challenge

The initial implementation used Lombok annotations for builders, constructors, and getters. During compilation, the build environment did not correctly recognize some Lombok-generated methods.

This caused issues such as missing constructors, missing getter methods, and missing builder methods.

### Solution

I removed the dependency on Lombok-generated behavior and replaced it with explicit Java code:

- Manual constructors
- Manual getters and setters
- Direct object creation instead of builder patterns
- Clear entity and request class structure

### Why This Was Important

This made the project more stable, easier to debug, and less dependent on IDE annotation processing configuration.

It also made the code more transparent because constructors and accessors are visible directly in the source files.

---

## 2. PostgreSQL and Docker Configuration

### Challenge

The Spring Boot application initially could not connect correctly to PostgreSQL because the expected database user and database were not available inside the running container.

The application expected:

```text
Database: cloudcost_db
User: cloudcost_user
Password: cloudcost_pass
```

However, the existing Docker volume had already initialized PostgreSQL with different settings.

### Solution

I updated the Docker Compose configuration and reset the PostgreSQL volume:

```bash
docker compose down -v
docker compose up -d
```

I also changed the host port mapping to avoid local PostgreSQL conflicts:

```yaml
ports:
  - "5433:5432"
```

Then the Spring Boot datasource was configured to use:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/cloudcost_db
    username: cloudcost_user
    password: cloudcost_pass
```

### Why This Was Important

This solved the database connection issue and created a repeatable local development setup using Docker.

It also showed the importance of understanding Docker volumes. PostgreSQL environment variables only initialize users and databases when the volume is created for the first time.

---

## 3. Building the Billing Data Model

### Challenge

The system needed to store cloud billing usage in a way that could support analytics and optimization features later.

Each billing record needed to include:

- User ownership
- Usage date
- Cloud service
- Resource ID
- Region
- Cost
- Usage hours
- CPU utilization

### Solution

I created a `BillingRecord` entity connected to a `User` entity through a many-to-one relationship.

This allowed every billing record to belong to a specific user and made it possible to query spending data by user.

### Why This Was Important

This created the foundation for later features:

- Total cost calculation
- Cost by service
- Idle resource detection
- Optimization recommendations
- Dashboard summary

---

## 4. Implementing Cost Analytics APIs

### Challenge

The backend needed to provide useful cloud cost insights, not just store billing records.

### Solution

I added APIs to calculate:

- Total cloud spend by user
- Cost grouped by service
- Idle resources based on usage behavior

Example endpoints:

```text
GET /api/billing/total?userId=1
GET /api/billing/cost-by-service?userId=1
GET /api/billing/idle?userId=1
```

### Why This Was Important

These APIs turned the project from basic CRUD into a cloud cost analytics backend.

They also made the data useful for dashboards and future frontend integration.

---

## 5. Adding CSV Billing Upload

### Challenge

Manually inserting one billing record at a time is not realistic for cloud billing data. Real cloud usage data is usually exported in bulk.

### Solution

I added a CSV upload API:

```text
POST /api/billing/upload?userId=1
```

The CSV parser reads rows, validates the expected columns, converts values into correct data types, and saves valid records into PostgreSQL.

The upload response includes:

- Total rows processed
- Saved rows
- Skipped rows
- Row-level errors

### Why This Was Important

This made the project more realistic because cloud billing data often comes from CSV exports.

It also introduced bulk data ingestion, validation, and partial failure handling.

---

## 6. Designing Idle Resource Detection

### Challenge

The project needed a simple way to identify wasteful or underused cloud resources.

### Solution

I implemented idle resource detection based on:

```text
CPU utilization below 5%
Usage hours at least 24
```

This logic identifies resources that are running for a long time but showing very low utilization.

### Why This Was Important

Idle resource detection is one of the most common cloud cost optimization use cases.

This feature makes the backend useful for identifying unnecessary cloud spending.

---

## 7. Generating Optimization Recommendations

### Challenge

Detecting idle resources is helpful, but the system also needed to explain what action should be taken.

### Solution

I added an optimization recommendation API:

```text
GET /api/billing/recommendations?userId=1
```

For each idle resource, the backend returns:

- Resource information
- Current cost
- Estimated savings
- Reason for the recommendation
- Suggested action

Example recommendations include:

- Stop or resize idle EC2 instances
- Downsize RDS databases
- Move old S3 data to cheaper storage
- Review Lambda memory and invocation patterns

### Why This Was Important

This made the project feel more like a real cloud cost optimization platform instead of just a reporting tool.

It also demonstrates business logic design and rule-based recommendation generation.

---

## 8. Building a Dashboard Summary API

### Challenge

A frontend dashboard should not need to call multiple APIs to show one summary page.

### Solution

I created a dashboard endpoint:

```text
GET /api/dashboard/summary?userId=1
```

This endpoint combines multiple backend calculations into one response:

- Total cost
- Cost by service
- Idle resource count
- Recommendation count
- Estimated monthly savings

### Why This Was Important

This API makes the backend easier to consume from a frontend application.

It also demonstrates aggregation logic and backend-for-frontend style API design.

---

## 9. Runtime Version and Endpoint Testing

### Challenge

After adding new APIs, the old Spring Boot process was sometimes still running on port 8080. This caused requests to hit an older version of the backend that did not include the newest endpoints.

### Solution

I stopped the old process and restarted the updated backend:

```bash
kill -9 $(lsof -ti :8080)
mvn clean spring-boot:run
```

### Why This Was Important

This helped verify that newly added endpoints were actually running from the latest compiled code.

It also reinforced the importance of managing local runtime processes during backend development.

---

## 10. Source Control and GitHub Publishing

### Challenge

The completed backend needed to be saved and shared through GitHub.

### Solution

I initialized Git, committed the completed project, created a GitHub repository, added the remote origin, and pushed the main branch.

```bash
git init
git branch -M main
git add .
git commit -m "Complete CloudCost Sentinel backend phases 1 to 4"
git remote add origin https://github.com/Thotasonali/cloudcost-sentinel
git push -u origin main
```

### Why This Was Important

This made the project available online and created a professional record of the completed backend implementation.

---

## Final Implemented Features

CloudCost Sentinel currently supports:

- User creation API
- Manual billing record creation
- PostgreSQL persistence
- Docker-based local database setup
- CSV billing upload
- Total cost calculation
- Cost by service analytics
- Idle resource detection
- Optimization recommendations
- Dashboard summary API
- GitHub project publishing

---

## Key Engineering Takeaways

This project strengthened my understanding of:

- Java Spring Boot backend development
- REST API design
- PostgreSQL integration
- Docker Compose setup
- JPA entity relationships
- CSV data ingestion
- Backend analytics logic
- Cloud cost optimization concepts
- API aggregation for dashboards
- Git and GitHub workflow
- Debugging build, database, and runtime issues
