-- =============================================================
--  Employee Leave Management System  –  data.sql
--  Sample seed dataset for 10 employees & leave requests
-- =============================================================
USE leave_management_db;

-- Clear previous test data
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE leave_requests;
TRUNCATE TABLE employees;
SET FOREIGN_KEY_CHECKS = 1;

-- ── 1. Insert 10 Sample Employees (with Roles & Passwords) ─────
INSERT INTO employees (id, name, email, department, role, password) VALUES
(1, 'Sarah Connor (HR Admin)', 'sarah.admin@example.com', 'Human Resources', 'ADMIN', 'admin123'),
(2, 'Alice Johnson', 'alice.johnson@example.com', 'Engineering', 'EMPLOYEE', 'password123'),
(3, 'Bob Smith', 'bob.smith@example.com', 'Human Resources', 'EMPLOYEE', 'password123'),
(4, 'Charlie Davis', 'charlie.davis@example.com', 'Finance', 'EMPLOYEE', 'password123'),
(5, 'Diana Prince', 'diana.prince@example.com', 'Marketing', 'EMPLOYEE', 'password123'),
(6, 'Evan Wright', 'evan.wright@example.com', 'Engineering', 'EMPLOYEE', 'password123'),
(7, 'Fiona Gallagher', 'fiona.gallagher@example.com', 'Product', 'EMPLOYEE', 'password123'),
(8, 'George Miller', 'george.miller@example.com', 'Operations', 'EMPLOYEE', 'password123'),
(9, 'Hannah Abbott', 'hannah.abbott@example.com', 'Engineering', 'EMPLOYEE', 'password123'),
(10, 'Ian Malcolm', 'ian.malcolm@example.com', 'Data Science', 'EMPLOYEE', 'password123');

-- ── 2. Insert Sample Leave Requests ───────────────────────────
INSERT INTO leave_requests (id, employee_id, leave_type, start_date, end_date, leave_days, status, reason) VALUES
-- Alice Johnson (id: 2): Total 7 days (Task 1: > 5 leave days)
(1, 2, 'CL', '2024-09-01', '2024-09-03', 3, 'APPROVED', 'Family occasion'),
(2, 2, 'EL', '2024-10-10', '2024-10-13', 4, 'APPROVED', 'Annual family vacation'),

-- Bob Smith (id: 3): Total 4 days
(3, 3, 'SL', '2024-09-05', '2024-09-06', 2, 'APPROVED', 'Medical checkup'),
(4, 3, 'CL', '2024-09-20', '2024-09-21', 2, 'PENDING',  'Personal emergency'),

-- Charlie Davis (id: 4): Total 2 days (Pending)
(5, 4, 'CL', '2024-09-15', '2024-09-16', 2, 'PENDING',  'Attending wedding ceremony'),

-- Diana Prince (id: 5): Total 1 day (Rejected)
(6, 5, 'EL', '2024-08-01', '2024-08-01', 1, 'REJECTED', 'Project critical deadline overlap'),

-- Fiona Gallagher (id: 7): Total 6 days (Task 1: > 5 leave days)
(7, 7, 'EL', '2024-11-04', '2024-11-09', 6, 'APPROVED', 'Recharge leave'),

-- George Miller (id: 8): Total 3 days (Pending)
(8, 8, 'SL', '2024-11-12', '2024-11-14', 3, 'PENDING',  'Seasonal flu recovery'),

-- Hannah Abbott (id: 9): Total 4 days (Approved)
(9, 9, 'CL', '2024-11-20', '2024-11-23', 4, 'APPROVED', 'Home renovation');

-- Note: Evan Wright (id: 6) and Ian Malcolm (id: 10) have NO leave requests (Task 4: Zero leaves)

ALTER TABLE employees AUTO_INCREMENT = 11;
ALTER TABLE leave_requests AUTO_INCREMENT = 10;
