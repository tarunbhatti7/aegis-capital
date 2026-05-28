# Aegis Capital - Transaction Management System

A secure, scalable transaction management system built with **Spring Boot 3.2** and **Java 17**. Supports deposits, withdrawals, fund transfers, transaction history, and role-based admin/user dashboards with a premium glassmorphism UI.

## Features

- **User Registration & Authentication** — Spring Security form login with BCrypt password encryption
- **Role-Based Access Control** — ROLE_USER (customer) and ROLE_ADMIN (administrator) with strict endpoint protection
- **Deposit / Withdraw / Transfer** — Full banking operations with SERIALIZABLE transaction isolation
- **Optimistic Locking** — JPA `@Version` prevents race conditions on concurrent transactions
- **Transaction History** — Color-coded debit/credit display with full audit trail
- **Admin Dashboard** — System-wide statistics, user management, transaction monitoring, user toggle (enable/disable cascades to accounts)
- **Premium UI** — Navy-gold glassmorphism design, responsive (4 breakpoints), staggered animations
- **Demo Data Seeding** — Auto-populates 4 users + 5 sample transactions on first run
- **Centralized Constants** — All strings in `AccountsConstants.java` — no magic strings
- **Comprehensive Logging** — Logback with daily rolling file appender
- **Global Exception Handling** — Graceful error pages for all exception types

## Tech Stack

| Component | Technology |
|-----------|-----------|
| **Backend** | Spring Boot 3.2.0 (JDK 17) |
| **Security** | Spring Security 6, BCryptPasswordEncoder |
| **ORM** | Spring Data JPA / Hibernate 6 |
| **Template Engine** | Thymeleaf + Thymeleaf Spring Security 6 Extras |
| **Frontend** | Bootstrap 5.3.2 + Custom CSS (938 lines) |
| **Database (prod)** | MySQL 8.x |
| **Database (dev)** | H2 In-Memory |
| **Build Tool** | Apache Maven |
| **Logging** | Logback (rolling file appender) |
| **Testing** | JUnit 5, Mockito |

## Prerequisites

- Java 17+ (JDK)
- Apache Maven 3.6+
- MySQL 8.x (optional — dev profile uses H2)
- Git

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/aegis-capital.git
cd aegis-capital
```

### 2. Configure Database (MySQL — default profile)

Update `src/main/resources/application.yml` with your MySQL credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/aegis_capital
    username: root
    password: your_password
```

Create the database:

```sql
CREATE DATABASE aegis_capital;
```

Tables are auto-generated via `ddl-auto: update`.

### 3. Run with Maven

```bash
mvn spring-boot:run
```

### Or use the dev profile (H2 in-memory — no MySQL needed)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 4. Access the application

```
http://localhost:8080
```

## Demo Credentials

| Username | Password | Role | Account Number | Initial Balance |
|----------|----------|------|---------------|-----------------|
| `admin` | `admin123` | ROLE_ADMIN | AEG0000000001 | $100,000.00 |
| `user1` | `password123` | ROLE_USER | AEG0000000002 | $5,000.00 |
| `user2` | `password123` | ROLE_USER | AEG0000000003 | $10,000.00 |
| `user3` | `password123` | ROLE_USER | AEG0000000004 | $7,500.00 |

## Project Structure

```
src/
├── main/
│   ├── java/com/aegis/capital/
│   │   ├── AegisCapitalApplication.java
│   │   ├── config/
│   │   │   ├── AccountsConstants.java      # Centralized constants
│   │   │   ├── DataInitializer.java        # Demo data seeder
│   │   │   ├── SecurityConfig.java         # Spring Security configuration
│   │   │   └── WebConfig.java              # MVC view controller
│   │   ├── controller/
│   │   │   ├── AuthController.java         # Login, registration
│   │   │   ├── UserController.java         # Customer operations
│   │   │   └── AdminController.java        # Admin operations
│   │   ├── dto/
│   │   │   ├── TransactionDTO.java         # Java record DTO
│   │   │   ├── TransactionMapper.java      # Entity to DTO mapper
│   │   │   └── TransactionRequest.java     # Form backing bean
│   │   ├── entity/
│   │   │   ├── User.java                   # JPA entity
│   │   │   ├── Account.java                # JPA entity (with @Version)
│   │   │   └── Transaction.java            # JPA entity
│   │   ├── exception/
│   │   │   ├── AccountNotFoundException.java
│   │   │   ├── InsufficientBalanceException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── AccountRepository.java
│   │   │   └── TransactionRepository.java
│   │   ├── service/
│   │   │   ├── CustomUserDetailsService.java
│   │   │   ├── UserService.java
│   │   │   ├── AccountService.java
│   │   │   └── TransactionService.java
│   │   └── util/
│   │       └── AccountNumberGenerator.java
│   └── resources/
│       ├── application.yml
│       ├── logback-spring.xml
│       ├── static/css/style.css             # Custom CSS (938 lines)
│       └── templates/                       # Thymeleaf templates
│           ├── login.html
│           ├── register.html
│           ├── user-dashboard.html
│           ├── account-details.html
│           ├── deposit.html
│           ├── withdraw.html
│           ├── transfer.html
│           ├── transaction-history.html
│           ├── admin-dashboard.html
│           ├── admin-users.html
│           ├── admin-user-detail.html
│           ├── admin-transactions.html
│           └── error.html
└── test/
    └── java/com/aegis/capital/
        ├── AegisCapitalApplicationTests.java
        └── service/
            └── TransactionServiceTest.java
```

## API Endpoints

### Authentication

| Method | Path | Description |
|--------|------|-------------|
| GET | `/login` | Login form |
| POST | `/login` | Authenticate (Spring Security) |
| GET | `/register` | Registration form |
| POST | `/register` | Create new user |
| GET | `/dashboard` | Role-based redirect |

### Customer (`/user`)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/user/dashboard` | User dashboard |
| GET | `/user/accounts` | View all accounts |
| GET | `/user/deposit` | Deposit form |
| POST | `/user/deposit` | Execute deposit |
| GET | `/user/withdraw` | Withdraw form |
| POST | `/user/withdraw` | Execute withdrawal |
| GET | `/user/transfer` | Transfer form |
| POST | `/user/transfer` | Execute transfer |
| GET | `/user/transactions` | Transaction history |

### Admin (`/admin`) — requires ROLE_ADMIN

| Method | Path | Description |
|--------|------|-------------|
| GET | `/admin/dashboard` | Admin dashboard (stats) |
| GET | `/admin/users` | List all users |
| GET | `/admin/users/{id}` | User detail |
| POST | `/admin/users/{id}/toggle-status` | Enable/disable user |
| GET | `/admin/transactions` | System-wide transaction log |

## Architecture

```
Client (Browser)
    |
Spring Security (AuthN / AuthZ)
    |
Controllers (MVC)
    |
Service Layer (Business Logic)
    |
Repository Layer (Spring Data JPA)
    |
Database (MySQL / H2)
```

- **Deposit/Withdraw/Transfer** use `@Transactional(isolation = Isolation.SERIALIZABLE)`
- **Optimistic locking** via `@Version` on `Account.balance` prevents race conditions
- **GlobalExceptionHandler** catches all exceptions with user-friendly error pages
- **DataInitializer** seeds demo data automatically when the database is empty

## Running Tests

```bash
mvn test
```

## Logging

Logs are written to `logs/aegis-capital.log` with daily rotation (30-day retention, max 10 MB per file).

## Screenshots

<!-- Add screenshots here -->
<!-- ![Login Page](screenshots/login.png) -->
<!-- ![Dashboard](screenshots/dashboard.png) -->
<!-- ![Admin Dashboard](screenshots/admin-dashboard.png) -->

## License

This project was developed as part of the Mphasis Limited Intern Program 2026 (1201_JAVA_BATCH, Bangalore).
