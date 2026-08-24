package com.example.leave_management.dto;

import com.example.leave_management.entity.LeaveStatus;
import com.example.leave_management.entity.LeaveType;

import java.time.LocalDate;

/**
 * Response body for all /api/leaves endpoints.
 * Flattens the Employee FK into employeeId + employeeName for convenience.
 */
public class LeaveResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeDepartment;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer leaveDays;
    private LeaveStatus status;
    private String reason;

    // ──────────── Constructors ────────────

    public LeaveResponse() {}

    public LeaveResponse(Long id, Long employeeId, String employeeName,
                         String employeeDepartment, LeaveType leaveType,
                         LocalDate startDate, LocalDate endDate,
                         Integer leaveDays, LeaveStatus status, String reason) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeDepartment = employeeDepartment;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveDays = leaveDays;
        this.status = status;
        this.reason = reason;
    }

    // ──────────── Getters & Setters ────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeDepartment() { return employeeDepartment; }
    public void setEmployeeDepartment(String employeeDepartment) { this.employeeDepartment = employeeDepartment; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getLeaveDays() { return leaveDays; }
    public void setLeaveDays(Integer leaveDays) { this.leaveDays = leaveDays; }

    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
