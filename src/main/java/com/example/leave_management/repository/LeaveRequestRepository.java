package com.example.leave_management.repository;

import com.example.leave_management.entity.LeaveRequest;
import com.example.leave_management.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // Find all leaves for a specific employee
    List<LeaveRequest> findByEmployeeId(Long employeeId);

    // Find leaves by status (e.g. PENDING)
    List<LeaveRequest> findByStatus(LeaveStatus status);

    // Find leaves by employee id and status
    List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);

    // Used in SQL task: employees with no leave requests
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.id = :employeeId")
    List<LeaveRequest> findLeavesByEmployeeId(Long employeeId);
}
