## Live Demo
https://college-transparency-portal.onrender.com
# College Transparency Portal

A full-stack web application built to solve real transparency problems at my college — manual attendance tracking, no way to verify marks after distribution, manual outpass approval, and untracked lab record submissions.

## Problem Statement

At PVPSIT, several academic processes are handled manually with no transparency or audit trail:
- Attendance is recorded manually and posted via Excel, with no way for students to verify accuracy
- Mid-term marks are distributed once with no way to check or dispute them afterward
- Outpass approval requires physical HOD signatures with no status visibility
- Lab record and assignment submissions are tracked manually with no overdue detection

This project builds a digital system addressing all four problems with real transparency and accountability features.

## Features

- **Student & Faculty Authentication** — role-based access control with BCrypt password hashing and session management
- **Attendance Tracking** — faculty marks attendance, students view real-time percentage calculations
- **Marks Management with Audit Log** — every mark change is permanently logged (old value, new value, who changed it, when) in an append-only audit table, directly solving the "can't verify marks later" problem
- **Outpass System** — digital request/approve/reject workflow with automatic escalation for requests pending too long
- **Submission Tracking** — faculty creates requirements, students mark submissions, automatic overdue detection
- **Global Exception Handling** — centralized error handling with clean, consistent API responses
- **Input Validation** — server-side validation using Bean Validation annotations

## Tech Stack

**Backend:** Java 17, Spring Boot 3.x, Spring Data JPA, Spring Security, Spring Validation
**Database:** MySQL 8.0
**Frontend:** Thymeleaf, HTML5, CSS3, Bootstrap 5
**Build Tool:** Maven
**Tools:** Git, GitHub, Postman, IntelliJ IDEA

## Architecture

The project follows a layered architecture:
Controller → Service → Repository → Database
- **Controllers** handle HTTP requests (both REST API and web page controllers)
- **Services** contain business logic, reused across REST endpoints, student-facing pages, and faculty-facing pages
- **Repositories** handle data access via Spring Data JPA
- **Entities** define the database schema, auto-generated via JPA

## Key Design Decisions

- **Append-only audit log for Marks**: rather than overwriting mark values on edit, every change is logged as a new row with old/new values, timestamp, and who made the change — enabling full transparency and dispute resolution
- **Time-based auto-escalation for Outpass**: pending requests automatically flag as urgent if unactioned past a threshold, calculated using Java's `ChronoUnit`
- **Role-based access control**: a single `Student` entity with a `role` field (STUDENT/FACULTY) determines page access and redirect behavior after login, with authorization checks on every protected endpoint — not just hidden UI links
- **Layered validation**: HTML `required` attributes for immediate user feedback, combined with server-side `@Valid` Bean Validation as the actual enforced security layer

## Getting Started

### Prerequisites
- Java 17
- MySQL 8.0
- Maven

### Setup
1. Clone the repository:
2. git clone https://github.com/mnagatarun/college-transparency-portal.git

2. Create a MySQL database:
```sql
   CREATE DATABASE college_portal;
```
3. Update `src/main/resources/application.properties` with your MySQL credentials
4. Run the application:

mvn spring-boot:run

5. Open `http://localhost:8080/login` in your browser

## Screenshots

[Add 2-3 screenshots here once available — login page, student dashboard, faculty portal]

## Known Limitations

- Faculty account creation is via an unlisted registration URL rather than admin-restricted creation — a pragmatic simplification for project scope
- Outpass escalation checks trigger on read (when the pending list is fetched) rather than via a background scheduled job
- No automated test suite yet (manual testing via Postman was used throughout development)

## Author

Munnaluri Naga Tarun — B.Tech CSE, Prasad V. Potluri Siddhartha Institute of Technology