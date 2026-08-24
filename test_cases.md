# Manual Test Case Specification & Execution Document

**Project**: Employee Leave Management System  
**Technology Stack**: Java 17, Spring Boot 3, MySQL, JUnit 5, HTML5/CSS3/JavaScript  

---

## Summary Matrix

| Test Case ID | Scenario | Priority | Automated Test | Manual Test Status |
|---|---|---|---|---|
| **TC-001** | Create employee with valid data | High | `EmployeeServiceTest` | **PASS** |
| **TC-002** | Create employee with blank name | High | `EmployeeServiceTest` | **PASS** |
| **TC-003** | Create employee with duplicate email | Medium | `EmployeeServiceTest` | **PASS** |
| **TC-004** | Create leave request with valid dates | High | `LeaveServiceTest` | **PASS** |
| **TC-005** | Create leave with invalid date range (`startDate > endDate`) | High | `LeaveServiceTest` | **PASS** |
| **TC-006** | Create leave with invalid leave type (e.g. `AL`) | High | `LeaveServiceTest` | **PASS** |
| **TC-007** | Auto calculate leave days (`endDate - startDate + 1`) | High | `LeaveServiceTest` | **PASS** |
| **TC-008** | Approve pending leave request (Admin action) | High | `LeaveServiceTest` | **PASS** |
| **TC-009** | Reject pending leave request (Admin action) | High | `LeaveServiceTest` | **PASS** |
| **TC-010** | Reject an already APPROVED leave request (Business Rule) | High | `LeaveServiceTest` | **PASS** |
| **TC-011** | Cancel a REJECTED leave request (Business Rule) | High | `LeaveServiceTest` | **PASS** |
| **TC-012** | Cancel an APPROVED or PENDING leave request | Medium | `LeaveServiceTest` | **PASS** |
| **TC-013** | Separated UI Portals & Authentication | High | Manual UI Verification | **PASS** |

---

## Detailed Test Cases

### TC-001: Create Employee with Valid Data
- **Test Objective**: Verify that an administrator can successfully register a new employee.
- **Precondition**: Application backend and MySQL database are running.
- **Test Steps**:
  1. Open Admin Portal.
  2. Click **"Add Employee"**.
  3. Fill Name: `Marcus Vance`, Email: `marcus.vance@example.com`, Department: `Engineering`, Role: `EMPLOYEE`.
  4. Click **"Save Account"**.
- **Expected Result**: HTTP `201 Created` returned. Employee appears in directory and dropdowns.
- **Actual Result**: `201 Created` with generated ID. Toast notification displayed.
- **Status**: **PASS**

---

### TC-002: Create Employee with Blank Name
- **Test Objective**: Verify business rule constraint that employee name cannot be blank.
- **Test Steps**:
  1. POST to `/api/employees` with `{ "name": "   ", "email": "test@test.com", "department": "HR" }`.
- **Expected Result**: HTTP `400 Bad Request` with `"Employee name cannot be blank"`.
- **Actual Result**: `400 Bad Request` returned with validation failure message.
- **Status**: **PASS**

---

### TC-003: Create Employee with Duplicate Email
- **Test Objective**: Verify unique constraint on employee email address.
- **Test Steps**:
  1. Register employee with existing email: `alice.johnson@example.com`.
- **Expected Result**: HTTP `400 Bad Request` stating email already exists.
- **Actual Result**: `400 Bad Request` - *"An employee with email 'alice.johnson@example.com' already exists"*.
- **Status**: **PASS**

---

### TC-004: Create Leave Request with Valid Dates
- **Test Objective**: Verify leave request creation and persistence.
- **Test Steps**:
  1. Sign in as employee (Alice Johnson).
  2. Click **"Send Leave Request"**.
  3. Select Type: `CL`, Start Date: `2024-11-10`, End Date: `2024-11-12`.
  4. Submit application.
- **Expected Result**: HTTP `201 Created`. Status defaults to `PENDING`. `leaveDays` calculated as 3.
- **Actual Result**: `201 Created`, `status: PENDING`, `leaveDays: 3`. Appears in employee table.
- **Status**: **PASS**

---

### TC-005: Create Leave with Invalid Date Range (`startDate > endDate`)
- **Test Objective**: Verify business rule that start date cannot be after end date.
- **Test Steps**:
  1. Submit leave request with Start Date: `2024-11-20`, End Date: `2024-11-15`.
- **Expected Result**: Client-side validation blocks submission; backend returns HTTP `400 Bad Request`.
- **Actual Result**: UI displays *"Start date cannot be after end date"*; HTTP 400 returned if called directly.
- **Status**: **PASS**

---

### TC-006: Create Leave with Invalid Leave Type
- **Test Objective**: Verify that only `CL`, `SL`, `EL` are accepted.
- **Test Steps**:
  1. POST to `/api/leaves` with `"leaveType": "AL"`.
- **Expected Result**: HTTP `400 Bad Request` - *"Invalid leave type: 'AL'. Allowed values are: CL, SL, EL"*.
- **Actual Result**: `400 Bad Request` with exact message.
- **Status**: **PASS**

---

### TC-007: Automatic Leave-Day Calculation
- **Test Objective**: Verify automatic duration formula `endDate - startDate + 1`.
- **Test Steps**:
  1. Select Start: `10-Aug`, End: `12-Aug`.
- **Expected Result**: Duration displays `3 Days`.
- **Actual Result**: Instant pop animation in UI showing `3 Days`, stored in database `leave_days = 3`.
- **Status**: **PASS**

---

### TC-008: Approve Pending Leave (Admin Action)
- **Test Objective**: Verify admin approval workflow.
- **Test Steps**:
  1. Sign in as Admin.
  2. Locate a `PENDING` leave request.
  3. Click **"Approve"**.
- **Expected Result**: HTTP `200 OK`. Leave status transitions to `APPROVED`.
- **Actual Result**: Status badge changes to green `APPROVED`.
- **Status**: **PASS**

---

### TC-009: Reject Pending Leave (Admin Action)
- **Test Objective**: Verify admin rejection workflow.
- **Test Steps**:
  1. Locate a `PENDING` request.
  2. Click **"Reject"**.
- **Expected Result**: HTTP `200 OK`. Leave status transitions to `REJECTED`.
- **Actual Result**: Status badge changes to red `REJECTED`.
- **Status**: **PASS**

---

### TC-010: Reject an already APPROVED Leave Request (Business Rule)
- **Test Objective**: Verify business rule: **Approved leave cannot be rejected**.
- **Test Steps**:
  1. PUT to `/api/leaves/{approvedId}/reject`.
- **Expected Result**: HTTP `400 Bad Request` - *"Cannot reject an already APPROVED leave request"*.
- **Actual Result**: HTTP 400 with exact business rule error message.
- **Status**: **PASS**

---

### TC-011: Cancel a REJECTED Leave Request (Business Rule)
- **Test Objective**: Verify business rule: **Rejected leave cannot be cancelled**.
- **Test Steps**:
  1. PUT to `/api/leaves/{rejectedId}/cancel`.
- **Expected Result**: HTTP `400 Bad Request` - *"Cannot cancel a REJECTED leave request"*.
- **Actual Result**: HTTP 400 with exact business rule error message.
- **Status**: **PASS**

---

### TC-012: Cancel an APPROVED or PENDING Leave Request
- **Test Objective**: Verify employee or admin can cancel active leaves.
- **Test Steps**:
  1. Click **"Cancel"** on an approved or pending leave.
- **Expected Result**: HTTP `200 OK`. Status becomes `CANCELLED`.
- **Actual Result**: Request transitioned to `CANCELLED`.
- **Status**: **PASS**

---

### TC-013: Separated Portals & Role-Based Access Control
- **Test Objective**: Verify strict separation between Admin and Employee portals.
- **Test Steps**:
  1. Login as Admin: Full approval/rejection actions, directory management, SQL explorer.
  2. Login as Employee: Personalized welcome, send leave request auto-locked to employee ID, cancel action, approval buttons hidden.
- **Expected Result**: UI strictly enforces role view and permissions.
- **Actual Result**: Clean white theme interface adapts seamlessly with dedicated views.
- **Status**: **PASS**
