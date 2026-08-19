# College Transparency Portal

A full-stack web application solving real transparency problems at my college — manual attendance tracking, no way to verify marks after distribution, manual outpass approval, and untracked lab/assignment submissions.

## Live Demo
https://college-transparency-portal.onrender.com

*(Hosted on a free tier — first load after inactivity may take 30-60 seconds to wake up.)*

**Test accounts** (roles auto-redirect after login):
- Admin: `mnagatarun@gmail.com`
- Faculty: `raviprakash@gmail.com`
- (Contact for passwords, or register a new student account directly)

## Problem Statement

At PVPSIT, several academic processes are handled manually with no transparency or audit trail:
- Attendance is recorded manually and posted via Excel, with no way for students to verify accuracy
- Mid-term marks are distributed once with no way to check or dispute them afterward
- Outpass approval requires physical HOD signatures with no status visibility
- Lab record and assignment submissions are tracked manually with no overdue detection

This project builds a digital system addressing all four problems with real transparency and accountability features.

## Features

- **Role-Based Authentication** — Student, Faculty, and Admin roles, with BCrypt password hashing and session management
- **Attendance Tracking** — faculty marks attendance, students view real-time percentage calculations
- **Marks Management with Audit Log** — every mark change is permanently logged (old value, new value, who changed it, when) in an append-only audit table, directly solving the "can't verify marks later" problem
- **Outpass System** — digital request/approve/reject workflow with automatic escalation (via scheduled background job) for requests pending too long
- **Submission Tracking** — faculty creates requirements, students mark submissions, automatic overdue detection (also scheduled)
- **Admin Panel** — create/manage Faculty accounts, enable/disable any user, reset passwords, view a global audit log across all students
- **Centralized Authorization** — a Spring `HandlerInterceptor` protects all `/faculty/**` and `/admin/**` routes at the request level, before reaching any controller
- **Login Rate Limiting** — accounts are temporarily locked after 5 failed login attempts
- **Global Exception Handling** — centralized error handling with clean, consistent responses
- **Input Validation** — server-side validation (Bean Validation + custom business rules) rejecting invalid data like negative marks
- **Structured Logging** — login attempts, mark changes, and approval actions logged via SLF4J
- **Unit Tests** — JUnit 5 + Mockito coverage for core business logic (attendance calculation, outpass workflow, marks audit logging)
- **Password Reset** — Admin-initiated reset (fully working); email-based self-service reset is implemented but currently blocked by an environment-specific SSL trust issue (see Known Limitations)

## Tech Stack

**Backend:** Java 17, Spring Boot 4.1, Spring Data JPA, Spring Security (BCrypt), Spring Validation, Spring Mail, SLF4J + Logback
**Database:** MySQL 8.0
**Frontend:** Thymeleaf, HTML5, CSS3, Bootstrap 5
**Testing:** JUnit 5, Mockito
**Build Tool:** Maven
**Deployment:** Docker, Render (app hosting), Railway (MySQL hosting)
**Tools:** Git, GitHub, Postman, IntelliJ IDEA

## Architecture

Layered architecture throughout:
```
Controller → Service → Repository → Database
```
Both REST API controllers (`@RestController`) and page controllers (`@Controller` + Thymeleaf) call the **same Service layer** — no duplicated business logic between the API, student pages, faculty pages, and admin pages.

## Key Design Decisions

- **Append-only audit log for Marks**: every change is logged as a new row with old/new values, timestamp, and who made the change — enabling full transparency and dispute resolution, without altering the current-state table.
- **Scheduled background jobs for escalation/overdue detection**: outpass escalation and submission overdue-flagging run hourly via Spring's `@Scheduled`, independent of whether anyone opens the app — not just checked on page load.
- **Role-based access control**: a single `Student` entity with a `role` field (STUDENT/FACULTY/ADMIN) determines redirect behavior after login.
- **Centralized authorization via HandlerInterceptor**: rather than repeating manual role checks in every controller method, a single `RoleInterceptor` protects all `/faculty/**` and `/admin/**` routes before requests reach any controller — this replaced an earlier per-method manual-check approach identified as fragile during a self-review, and also eliminated duplicated authorization code across controllers.
- **Admin-controlled account provisioning**: Faculty accounts are created exclusively by Admin through a protected panel — replacing an earlier, less secure "unlisted registration URL" approach used during initial development.
- **Layered validation**: HTML `required` attributes for immediate user feedback, combined with server-side `@Valid` Bean Validation and custom business-rule checks (e.g., marks cannot be negative or exceed max) as the actual enforced layer.
- **Environment-based configuration**: database and mail credentials are externalized via environment variables, never hardcoded — the same codebase runs against a local MySQL instance in development and a Railway-hosted MySQL instance in production.

## Getting Started (Local Development)

### Prerequisites
- Java 17
- MySQL 8.0
- Maven (or use the included `mvnw` wrapper)

### Setup
1. Clone the repository:
   ```
   git clone https://github.com/mnagatarun/college-transparency-portal.git
   ```
2. Create a MySQL database:
   ```sql
   CREATE DATABASE college_portal;
   ```
3. Set the following environment variables (in your IDE's run configuration, or your shell):
   ```
   DB_URL=jdbc:mysql://localhost:3306/college_portal
   DB_USERNAME=root
   DB_PASSWORD=your_local_mysql_password
   MAIL_USERNAME=your_gmail@gmail.com
   MAIL_PASSWORD=your_gmail_app_password
   APP_BASE_URL=http://localhost:8080
   ```
4. Run the application:
   ```
   ./mvnw spring-boot:run
   ```
5. Open `http://localhost:8080/login` in your browser

## Deployment

Deployed via Docker on Render, with MySQL hosted on Railway. Both services use environment variables for configuration — no credentials are committed to the repository. See the `Dockerfile` for the containerization setup.

## Known Limitations

- **Email-based password reset** is implemented (token generation, expiry, email dispatch via Spring Mail) but currently blocked by an SSL certificate trust issue in the local Java environment (`PKIX path building failed`), likely caused by network-level SSL inspection. Admin-initiated password reset remains fully functional as the primary recovery path.
- **Admin accounts have no self-service recovery path** — if all Admin accounts are locked out, recovery requires direct database access to promote a new account. In production, this would be mitigated by maintaining multiple Admin accounts or completing the email-reset flow.
- **CSRF protection is disabled**, since the application combines session-based page auth with a REST API rather than traditional form-only submission. A production version would re-enable it using Thymeleaf's built-in CSRF token support.
- **Rate limiting is in-memory**, not persisted — resets on application restart. A production version would use a persistent store (e.g., Redis or a database table).
- **No automated frontend/integration tests** — testing was done via JUnit/Mockito for business logic, and manual end-to-end testing (Postman + browser) for the full stack.
- Early commit history contains a local development database password, since rotated; production credentials were always managed via environment variables and were never committed.

## Author

M.NagaTarun — B.Tech CSE, Prasad V. Potluri Siddhartha Institute of Technology
