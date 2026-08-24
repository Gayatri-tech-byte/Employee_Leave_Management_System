-- =============================================================
--  Employee Leave Management System  –  schema.sql
--  Database: leave_management_db
--  Run this manually or let Hibernate create the tables via
--  spring.jpa.hibernate.ddl-auto=update
-- =============================================================

CREATE DATABASE IF NOT EXISTS leave_management_db;
USE leave_management_db;

-- ── 1. employees table ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS employees (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL,
    department VARCHAR(100) NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'EMPLOYEE' COMMENT 'ADMIN | EMPLOYEE',
    password   VARCHAR(255) NOT NULL DEFAULT 'password123',

    CONSTRAINT pk_employees PRIMARY KEY (id),
    CONSTRAINT uq_employees_email UNIQUE (email),
    CONSTRAINT ck_employee_role   CHECK (role IN ('ADMIN', 'EMPLOYEE'))
);

-- ── 2. leave_requests table ───────────────────────────────────
CREATE TABLE IF NOT EXISTS leave_requests (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    employee_id BIGINT      NOT NULL,
    leave_type  VARCHAR(5)  NOT NULL COMMENT 'CL | SL | EL',
    start_date  DATE        NOT NULL,
    end_date    DATE        NOT NULL,
    leave_days  INT         NOT NULL COMMENT 'end_date - start_date + 1',
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                COMMENT 'PENDING | APPROVED | REJECTED | CANCELLED',
    reason      TEXT        NULL,

    CONSTRAINT pk_leave_requests   PRIMARY KEY (id),
    CONSTRAINT fk_lr_employee      FOREIGN KEY (employee_id)
        REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT ck_leave_type       CHECK (leave_type IN ('CL', 'SL', 'EL')),
    CONSTRAINT ck_leave_status     CHECK (status IN ('PENDING','APPROVED','REJECTED','CANCELLED')),
    CONSTRAINT ck_date_order       CHECK (end_date >= start_date)
);
