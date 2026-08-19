# College Transparency Portal

A full-stack web app I built to fix a few things that annoyed me about how my college handles day-to-day academic admin: attendance kept on paper/Excel, mid-term marks that get handed out once with no way to check them later, outpass approval that needs a physical HOD signature, and lab/assignment submissions nobody actually tracks.

## Live Demo

https://college-transparency-portal.onrender.com

(Hosted on a free tier, so the first load after inactivity can take 30-60 seconds while it wakes up.)

**Test accounts** (roles auto-redirect after login):
- Admin: `admin@test.com`
- Faculty: `faculty@test.com`

For the fastest way to try it out, just register a new student account directly.

## Problem Statement

At PVPSIT, a few academic processes are still handled manually with basically no audit trail:

- Attendance gets recorded by hand and posted via Excel — students have no way to check it's accurate.
- Mid-term marks are given out once. If something looks wrong, there's no record to dispute it against.
- Outpass approval needs a physical HOD signature, and there's no way to check the status in between.
- Lab records and assignments are tracked manually, so nobody catches overdue submissions until it's too late.

This project is a digital system that tries to solve all four with actual transparency and an audit trail.

## Features

- **Role-based authentication** — Student, Faculty, and Admin roles, BCrypt password hashing, session management.
- **Attendance tracking** — faculty mark attendance, students see live percentage calculations.
- **Marks management with audit log.** This is the core feature I cared about most: every mark change gets logged permanently in an append-only table (old value, new value, who changed it, when). Solves the "can't verify marks after the fact" problem directly.
- **Outpass system** — digital request/approve/reject flow, with a scheduled job that auto-escalates requests that sit pending too long.
- **Submission tracking** — faculty set requirements, students mark submissions, overdue ones get flagged automatically (also via a scheduled job).
- **Admin panel** — create/manage faculty accounts, enable or disable any user, reset passwords, view a global audit log across all students.
- **Centralized authorization** — a Spring `HandlerInterceptor` guards every `/faculty/**` and `/admin/**` route at the request level, before it ever reaches a controller.
- **Login rate limiting** — accounts lock temporarily after 5 failed attempts.
- **Global exception handling** for consistent error responses.
- **Input validation** — server-side (Bean Validation + custom rules), so things like negative marks get rejected no matter what the frontend does.
- **Structured logging** — login attempts, mark changes, and approvals go through SLF4J.
- **Unit tests** — JUnit 5 + Mockito covering the core business logic (attendance calc, outpass workflow, marks audit logging).
- **Password reset** — admin-initiated reset works fully. Self-service email reset is built (token generation, expiry, dispatch via Spring Mail) but currently blocked by an SSL trust issue in my local environment — details below.

## Tech Stack

**Backend:** Java 17, Spring Boot 4.1, Spring Data JPA, Spring Security (BCrypt), Spring Validation, Spring Mail, SLF4J + Logback
**Database:** MySQL 8.0
**Frontend:** Thymeleaf, HTML5, CSS3, Bootstrap 5
**Testing:** JUnit 5, Mockito
**Build:** Maven
**Deployment:** Docker, Render (app hosting), Railway (MySQL hosting)
**Tools:** Git, GitHub, Postman, IntelliJ IDEA

## Architecture

Standard layered setup:

```
Controller → Service → Repository → Database
```

Both the REST API controllers (`@RestController`) and the page controllers (`@Controller` + Thymeleaf) call into the same service layer, so there's no duplicated business logic between the API and the student/faculty/admin pages.

## Some design decisions I made along the way

**Append-only audit log for marks.** Every change is logged as a new row (old value, new value, timestamp, who made it) instead of overwriting the current value. That's what actually makes disputes possible after the fact.

**Scheduled jobs, not just page-load checks.** Outpass escalation and submission overdue-flagging run hourly via `@Scheduled`, so they happen whether or not anyone's actually using the app that day.

**Single Student entity with a role field.** STUDENT / FACULTY / ADMIN all live in one table, and the role just determines where you land after login. Simpler than three separate entities for what's mostly the same shape of data.

**Moved authorization into one interceptor.** I originally had manual role checks scattered in each controller method. During a self-review I realized that was fragile — easy to forget a check somewhere — so I replaced it with a single `RoleInterceptor` that guards all `/faculty/**` and `/admin/**` routes before they hit any controller. Also killed a bunch of duplicated code in the process.

**Admin-only account creation for faculty.** Early on I had an "unlisted registration URL" for faculty signups, which in hindsight was a bad idea security-wise. Switched to admin-only provisioning through a protected panel instead.

**Validation on both ends.** HTML `required` attributes give instant feedback in the browser, but the actual enforcement is server-side — `@Valid` Bean Validation plus custom rules (e.g. marks can't go negative or above the max).

**Config via environment variables.** DB and mail credentials are never hardcoded. Same codebase runs against local MySQL in dev and Railway MySQL in production, just by swapping env vars.

## Getting Started (Local Development)

### Prerequisites
- Java 17
- MySQL 8.0
- Maven (or use the included `mvnw` wrapper)

### Setup

1. Clone the repo:
   ```
   git clone https://github.com/mnagatarun/college-transparency-portal.git
   ```
2. Create a MySQL database:
   ```sql
   CREATE DATABASE college_portal;
   ```
3. Set these environment variables (IDE run config or shell):
   ```
   DB_URL=jdbc:mysql://localhost:3306/college_portal
   DB_USERNAME=root
   DB_PASSWORD=your_local_mysql_password
   MAIL_USERNAME=your_gmail@gmail.com
   MAIL_PASSWORD=your_gmail_app_password
   APP_BASE_URL=http://localhost:8080
   ```
4. Run it:
   ```
   ./mvnw spring-boot:run
   ```
5. Open `http://localhost:8080/login`.

## Deployment

Runs in Docker on Render, with MySQL on Railway. Both use environment variables for config — nothing sensitive is committed to the repo. See the `Dockerfile` for the container setup.

## Known Limitations

- **Email password reset isn't live yet.** The whole flow is built (token generation, expiry, dispatch via Spring Mail) but blocked locally by an SSL cert issue (`PKIX path building failed`), probably from network-level SSL inspection on my end. Admin-initiated reset is the working fallback for now.
- **No self-service recovery for admin accounts.** If every admin gets locked out, the only fix right now is direct DB access to promote a new one. In production I'd want multiple admin accounts as a safety net, plus the email flow finished.
- **CSRF protection is off.** The app mixes session-based page auth with a REST API, which doesn't play nicely with default CSRF setup. A production version should re-enable it using Thymeleaf's built-in CSRF token support.
- **Rate limiting is in-memory** — resets on restart. Should move to something persistent (Redis, or a DB table) for production.
- **No automated frontend/integration tests.** Business logic is covered by JUnit/Mockito; the rest was manual testing through Postman and the browser.
- **Old commit history briefly had a local dev DB password in it** — since rotated. Production credentials were always in environment variables and never committed.

## Author

M. NagaTarun — B.Tech IT, Prasad V. Potluri Siddhartha Institute of Technology
[GitHub](https://github.com/mnagatarun)
