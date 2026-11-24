
# Employee Leave Management System

A web-based Employee Leave Management System built with Java, Spring Boot, Spring Data JPA, MySQL, and HTML, CSS, and JavaScript.

The application provides separate Admin and Employee portals to manage employee records and leave requests.

## Features

### Admin Portal
- Register and view employee accounts.
- View employee leave requests.
- Approve or reject pending leave applications.
- Access leave-related SQL reports.

### Employee Portal
- Submit leave applications.
- View personal leave request history.
- Track leave application status.
- Cancel eligible leave requests.

### Business Rules and Validation
- Supports Casual Leave (CL), Sick Leave (SL), and Earned Leave (EL).
- Validates leave start and end dates.
- Automatically calculates leave duration.
- Prevents invalid leave status transitions.
- Handles invalid requests and application errors.

## Technology Stack

| Area | Technologies |
|---|---|
| Backend | Java 24, Spring Boot 3.4.13 |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL 8 |
| Frontend | HTML5, CSS3, JavaScript |
| API | REST APIs |
| Build Tool | Apache Maven |
| Testing | JUnit 5, Mockito |

## Project Structure

```text
Employee_Leave_Management_System/
├── database/
│   ├── schema.sql
│   ├── queries.sql
│   └── data.sql
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/leave_management/
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── repository/
│   │   │       ├── entity/
│   │   │       ├── dto/
│   │   │       └── exception/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           ├── index.html
│   │           ├── style.css
│   │           └── app.js
│   └── test/
├── screenshots/
├── pom.xml
├── test_cases.md
└── README.md
```

## Getting Started

### Prerequisites

Install the following:

- JDK 24
- Apache Maven
- MySQL Server 8.0 or later
- Git

Verify Java and Maven:

```bash
java -version
mvn -version
```

### 1. Clone the Repository

```bash
git clone https://github.com/Gayatri-tech-byte/Employee_Leave_Management_System.git
```

Navigate into the project:

```bash
cd Employee_Leave_Management_System
```

### 2. Configure MySQL

Make sure your MySQL server is running.

The application uses the following database configuration:

- Database: `leave_management_db`
- Username: `root`
- Password: Supplied through the `DB_PASSWORD` environment variable.

The database URL is configured to create the database if it does not already exist.

### 3. Configure the Database Password

The application reads the database password from an environment variable instead of storing it directly in the configuration file.

**Windows PowerShell:**

```powershell
$env:DB_PASSWORD = Read-Host "Enter MySQL root password"
```

**Linux / macOS:**

```bash
export DB_PASSWORD='your_mysql_password'
```

Replace the example value with your local MySQL password. Do not commit passwords or `.env` files to GitHub.

### 4. Build the Application

```bash
mvn clean package
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

Open your browser:

```text
http://localhost:8080
```

## REST API Overview

### Authentication

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/login` | User login |
| GET | `/api/auth/users` | Retrieve available demo accounts |

### Employee Management

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/employees` | Register an employee |
| GET | `/api/employees` | Retrieve employees |
| GET | `/api/employees/{id}` | Retrieve an employee by ID |

### Leave Management

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/leaves` | Submit a leave request |
| GET | `/api/leaves` | Retrieve leave requests |
| GET | `/api/leaves/employee/{employeeId}` | Retrieve employee leave history |
| GET | `/api/leaves/{id}` | Retrieve a leave request |
| PUT | `/api/leaves/{id}/approve` | Approve a leave request |
| PUT | `/api/leaves/{id}/reject` | Reject a leave request |
| PUT | `/api/leaves/{id}/cancel` | Cancel an eligible leave request |

## Database

The application uses MySQL with two main tables:

- `employees` — stores employee details and roles.
- `leave_requests` — stores leave applications, dates, duration, and status.

Additional SQL queries are available in `database/queries.sql`.

Sample database data is available in `database/data.sql`.

## Testing

The project includes automated tests using JUnit 5 and Mockito.

Run the tests with:

```bash
mvn test
```

## My Contributions

My work on this repository includes:

- Updated the Spring Boot version to 3.4.13.
- Configured the project to use Java 24.
- Replaced the hardcoded database password with the `DB_PASSWORD` environment variable.
- Updated `.gitignore` to exclude environment files and log files.
- Built and verified the project using Maven.

## Source Acknowledgement

This project is based on an existing Employee Leave Management System originally published by [prudhvi209](https://github.com/prudhvi209/Employee_Leave_Management).

This repository is maintained separately, and the changes listed above describe my contributions. The original source is acknowledged to preserve attribution.

---

**Repository:** [Employee_Leave_Management_System](https://github.com/Gayatri-tech-byte/Employee_Leave_Management_System)
