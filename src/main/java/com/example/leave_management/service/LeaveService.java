package com.example.leave_management.service;

import com.example.leave_management.dto.LeaveRequest;
import com.example.leave_management.dto.LeaveResponse;
import com.example.leave_management.entity.Employee;
import com.example.leave_management.entity.LeaveStatus;
import com.example.leave_management.entity.LeaveType;
import com.example.leave_management.exception.BadRequestException;
import com.example.leave_management.exception.ResourceNotFoundException;
import com.example.leave_management.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeaveService {

    private final LeaveRequestRepository leaveRepo;
    private final EmployeeService employeeService;   // reuse findOrThrow

    public LeaveService(LeaveRequestRepository leaveRepo, EmployeeService employeeService) {
        this.leaveRepo = leaveRepo;
        this.employeeService = employeeService;
    }

    // ── Create Leave Request ──────────────────────────────────────────────────
    public LeaveResponse createLeave(LeaveRequest request) {

        // 1. Validate employee exists → 404 if not
        Employee employee = employeeService.findOrThrow(request.getEmployeeId());

        // 2. Validate leave type = CL / SL / EL
        LeaveType leaveType = parseLeaveType(request.getLeaveType());

        // 3. Validate date fields present
        if (request.getStartDate() == null) {
            throw new BadRequestException("Start date is required");
        }
        if (request.getEndDate() == null) {
            throw new BadRequestException("End date is required");
        }

        // 4. Business rule: startDate cannot be after endDate
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException(
                    "Start date (" + request.getStartDate() + ") cannot be after end date (" + request.getEndDate() + ")");
        }

        // 5. Calculate leave days automatically: endDate - startDate + 1
        int leaveDays = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        // 6. Build and persist entity
        com.example.leave_management.entity.LeaveRequest entity = new com.example.leave_management.entity.LeaveRequest();
        entity.setEmployee(employee);
        entity.setLeaveType(leaveType);
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setLeaveDays(leaveDays);
        entity.setStatus(LeaveStatus.PENDING);
        entity.setReason(request.getReason());

        return toResponse(leaveRepo.save(entity));
    }

    // ── Get All Leave Requests ────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<LeaveResponse> getAllLeaves() {
        return leaveRepo.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Get Leaves By Employee ID (For Employee Self-Service) ─────────────────
    @Transactional(readOnly = true)
    public List<LeaveResponse> getLeavesByEmployee(Long employeeId) {
        employeeService.findOrThrow(employeeId); // verify employee exists
        return leaveRepo.findByEmployeeId(employeeId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Get Leave Request by ID ───────────────────────────────────────────────
    @Transactional(readOnly = true)
    public LeaveResponse getLeaveById(Long id) {
        return toResponse(findLeaveOrThrow(id));
    }

    // ── Approve ───────────────────────────────────────────────────────────────
    public LeaveResponse approveLeave(Long id) {
        com.example.leave_management.entity.LeaveRequest leave = findLeaveOrThrow(id);

        // Only PENDING can be approved
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException(
                    "Cannot approve leave in status: " + leave.getStatus() +
                    ". Only PENDING leaves can be approved.");
        }

        leave.setStatus(LeaveStatus.APPROVED);
        return toResponse(leaveRepo.save(leave));
    }

    // ── Reject ────────────────────────────────────────────────────────────────
    public LeaveResponse rejectLeave(Long id) {
        com.example.leave_management.entity.LeaveRequest leave = findLeaveOrThrow(id);

        // Business rule: APPROVED leave cannot be rejected
        if (leave.getStatus() == LeaveStatus.APPROVED) {
            throw new BadRequestException(
                    "Cannot reject an already APPROVED leave request. " +
                    "Approved leave can only be cancelled.");
        }

        // Only PENDING can be rejected (CANCELLED and REJECTED are also invalid)
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException(
                    "Cannot reject leave in status: " + leave.getStatus() +
                    ". Only PENDING leaves can be rejected.");
        }

        leave.setStatus(LeaveStatus.REJECTED);
        return toResponse(leaveRepo.save(leave));
    }

    // ── Cancel ────────────────────────────────────────────────────────────────
    public LeaveResponse cancelLeave(Long id) {
        com.example.leave_management.entity.LeaveRequest leave = findLeaveOrThrow(id);

        // Business rule: APPROVED leave cannot be cancelled
        if (leave.getStatus() == LeaveStatus.APPROVED) {
            throw new BadRequestException(
                    "Cannot cancel an already APPROVED leave request. Only PENDING leaves can be cancelled.");
        }

        // Business rule: REJECTED leave cannot be cancelled
        if (leave.getStatus() == LeaveStatus.REJECTED) {
            throw new BadRequestException(
                    "Cannot cancel a REJECTED leave request.");
        }

        // CANCELLED leave cannot be cancelled again
        if (leave.getStatus() == LeaveStatus.CANCELLED) {
            throw new BadRequestException(
                    "Leave is already cancelled.");
        }

        leave.setStatus(LeaveStatus.CANCELLED);
        return toResponse(leaveRepo.save(leave));
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private com.example.leave_management.entity.LeaveRequest findLeaveOrThrow(Long id) {
        return leaveRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
    }

    /**
     * Converts the raw String from the request DTO into the LeaveType enum.
     * Returns a friendly 400 for unsupported values like "AL" or "PL".
     */
    private LeaveType parseLeaveType(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BadRequestException("Leave type is required. Allowed values: CL, SL, EL");
        }
        try {
            return LeaveType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Invalid leave type: '" + raw + "'. Allowed values are: CL, SL, EL");
        }
    }

    private LeaveResponse toResponse(com.example.leave_management.entity.LeaveRequest lr) {
        return new LeaveResponse(
                lr.getId(),
                lr.getEmployee().getId(),
                lr.getEmployee().getName(),
                lr.getEmployee().getDepartment(),
                lr.getLeaveType(),
                lr.getStartDate(),
                lr.getEndDate(),
                lr.getLeaveDays(),
                lr.getStatus(),
                lr.getReason()
        );
    }
}
