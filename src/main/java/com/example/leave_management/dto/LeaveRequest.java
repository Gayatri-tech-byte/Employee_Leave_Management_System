package com.example.leave_management.dto;

import com.example.leave_management.entity.LeaveType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Request body for POST /api/leaves
 *
 * Note: leaveType is received as a String so we can give a friendly error
 * message when an invalid value (e.g. "AL") is supplied, rather than a
 * raw Jackson 400.  Conversion to the enum is done in the service layer.
 */
public class LeaveRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Leave type is required")
    private String leaveType;   // "CL" | "SL" | "EL"  – validated in service

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private String reason;

    // ──────────── Getters & Setters ────────────

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
