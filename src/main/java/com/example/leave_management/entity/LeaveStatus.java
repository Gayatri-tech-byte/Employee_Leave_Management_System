package com.example.leave_management.entity;

/**
 * Lifecycle states of a LeaveRequest.
 * Allowed transitions (business rules):
 *   PENDING  → APPROVED
 *   PENDING  → REJECTED
 *   PENDING  → CANCELLED
 *   APPROVED → CANCELLED
 *
 * Forbidden transitions:
 *   APPROVED → REJECTED  (assignment rule)
 *   REJECTED → CANCELLED (assignment rule)
 */
public enum LeaveStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED
}
