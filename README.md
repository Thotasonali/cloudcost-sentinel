# CloudCost Sentinel — Phase 1

CloudCost Sentinel is a Spring Boot + Java backend project for cloud billing analysis and cloud cost optimization.

This Phase 1 ZIP includes the project foundation:

- Spring Boot application
- PostgreSQL Docker setup
- User entity and repository
- Billing record entity and repository
- Temporary dev user APIs
- Billing APIs for manual billing record creation
- Cost summary APIs
- Idle resource detection API
- Basic validation and global exception handling

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- Lombok
- Docker Compose
- Maven

## Project Structure

```text
src/main/java/com/cloudcost/sentinel
├── CloudCostSentinelApplication.java
├── billing
│   ├── BillingController.java
│   ├── BillingRecord.java
│   ├── BillingRepository.java
│   ├── BillingService.java
│   └── CreateBillingRecordRequest.java
├── config
│   └── SecurityConfig.java
├── exception
│   ├── ApiError.java
│   └── GlobalExceptionHandler.java
└── user
    ├── CreateUserRequest.java
    ├── DevUserController.java
    ├── Role.java
    ├── User.java
    └── UserRepository.java
```

## How to Run

### 1. Start PostgreSQL

```bash
docker compose up -d
```

### 2. Run Spring Boot

```bash
mvn spring-boot:run
```

The backend will run at:

```text
http://localhost:8080
```

### 3. Health Check

```http
GET http://localhost:8080/actuator/health
```

## Test APIs

You can use the ready-made file:

```text
docs/api-tests.http
```

This file works in IntelliJ IDEA, VS Code REST Client extension, or similar tools.

## API Examples

### Create Temporary Dev User

```http
POST http://localhost:8080/api/dev/users
Content-Type: application/json

{
  "name": "Sonali",
  "email": "sonali@example.com",
  "password": "test123"
}
```

### Add Billing Record

```http
POST http://localhost:8080/api/billing
Content-Type: application/json

{
  "userId": 1,
  "usageDate": "2026-07-01",
  "service": "EC2",
  "resourceId": "i-12345",
  "region": "us-east-1",
  "cost": 12.50,
  "usageHours": 24,
  "cpuUtilization": 3.2
}
```

### Get Total Cost

```http
GET http://localhost:8080/api/billing/total?userId=1
```

Expected response:

```json
{
  "totalCost": 12.50
}
```

### Get Cost by Service

```http
GET http://localhost:8080/api/billing/cost-by-service?userId=1
```

### Get Idle Resources

```http
GET http://localhost:8080/api/billing/idle?userId=1
```

Idle resource rule in Phase 1:

```text
cpuUtilization < 5 AND usageHours >= 24
```

## Important Phase 1 Note

Security is temporarily open in `SecurityConfig.java`:

```java
.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
```

This is intentional for quick testing. In a later phase, this will be replaced with:

- `/api/auth/register`
- `/api/auth/login`
- JWT token validation
- Protected user-specific endpoints

## Next Phase

Phase 2 should add CSV upload:

- Upload cloud billing CSV
- Parse rows
- Save records in PostgreSQL
- Return upload summary
- Handle invalid rows cleanly

