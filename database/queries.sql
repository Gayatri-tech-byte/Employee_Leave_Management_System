-- =============================================================
--  Employee Leave Management System  –  queries.sql
--  SQL tasks required by the assignment
-- =============================================================
USE leave_management_db;

-- ── Q1: Employees with more than 5 leave days ─────────────────
SELECT
    e.id,
    e.name,
    e.department,
    SUM(lr.leave_days) AS total_leave_days
FROM employees e
JOIN leave_requests lr ON lr.employee_id = e.id
GROUP BY e.id, e.name, e.department
HAVING SUM(lr.leave_days) > 5
ORDER BY total_leave_days DESC;

-- ── Q2: Total leave taken by each employee ────────────────────
SELECT
    e.id,
    e.name,
    e.department,
    COALESCE(SUM(lr.leave_days), 0) AS total_leave_days,
    COUNT(lr.id)                     AS total_requests
FROM employees e
LEFT JOIN leave_requests lr ON lr.employee_id = e.id
GROUP BY e.id, e.name, e.department
ORDER BY total_leave_days DESC;

-- ── Q3: Pending leave requests ────────────────────────────────
SELECT
    lr.id,
    e.name  AS employee_name,
    e.department,
    lr.leave_type,
    lr.start_date,
    lr.end_date,
    lr.leave_days,
    lr.reason
FROM leave_requests lr
JOIN employees e ON e.id = lr.employee_id
WHERE lr.status = 'PENDING'
ORDER BY lr.start_date;

-- ── Q4: Employees with NO leave requests ──────────────────────
SELECT
    e.id,
    e.name,
    e.email,
    e.department
FROM employees e
LEFT JOIN leave_requests lr ON lr.employee_id = e.id
WHERE lr.id IS NULL
ORDER BY e.name;

-- ── Q5: Most frequently used leave type ───────────────────────
SELECT
    leave_type,
    COUNT(*) AS usage_count
FROM leave_requests
GROUP BY leave_type
ORDER BY usage_count DESC
LIMIT 1;
