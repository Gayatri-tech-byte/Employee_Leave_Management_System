package com.example.leave_management.entity;

/**
 * Roles for Authorization & Access Control:
 *  ADMIN    – Can approve/reject leaves, register employees, view SQL analytics and full reports.
 *  EMPLOYEE – Can apply for leaves, view their own leave status, and cancel their own pending leaves.
 */
public enum Role {
    ADMIN,
    EMPLOYEE
}
