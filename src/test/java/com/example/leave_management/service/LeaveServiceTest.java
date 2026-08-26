package com.example.leave_management.service;

import com.example.leave_management.dto.LeaveRequest;
import com.example.leave_management.dto.LeaveResponse;
import com.example.leave_management.entity.Employee;
import com.example.leave_management.entity.LeaveStatus;
import com.example.leave_management.entity.LeaveType;
import com.example.leave_management.entity.Role;
import com.example.leave_management.exception.BadRequestException;
import com.example.leave_management.exception.ResourceNotFoundException;
import com.example.leave_management.repository.LeaveRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock
    private LeaveRequestRepository leaveRepo;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private LeaveService leaveService;

    private Employee employee;
    private com.example.leave_management.entity.LeaveRequest leaveEntity;
    private LeaveRequest request;

    @BeforeEach
    void setUp() {
        employee = new Employee("Alice Johnson", "alice@example.com", "Engineering", Role.EMPLOYEE);
        employee.setId(1L);

        leaveEntity = new com.example.leave_management.entity.LeaveRequest();
        leaveEntity.setId(10L);
        leaveEntity.setEmployee(employee);
        leaveEntity.setLeaveType(LeaveType.CL);
        leaveEntity.setStartDate(LocalDate.of(2024, 9, 10));
        leaveEntity.setEndDate(LocalDate.of(2024, 9, 12));
        leaveEntity.setLeaveDays(3);
        leaveEntity.setStatus(LeaveStatus.PENDING);
        leaveEntity.setReason("Personal vacation");

        request = new LeaveRequest();
        request.setEmployeeId(1L);
        request.setLeaveType("CL");
        request.setStartDate(LocalDate.of(2024, 9, 10));
        request.setEndDate(LocalDate.of(2024, 9, 12));
        request.setReason("Personal vacation");
    }

    @Test
    @DisplayName("Scenario 1: Leave creation succeeds & auto-calculates 3 leave days")
    void testCreateLeave_Success() {
        when(employeeService.findOrThrow(1L)).thenReturn(employee);
        when(leaveRepo.save(any(com.example.leave_management.entity.LeaveRequest.class))).thenReturn(leaveEntity);

        LeaveResponse response = leaveService.createLeave(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(3, response.getLeaveDays());
        assertEquals(LeaveStatus.PENDING, response.getStatus());
        assertEquals(LeaveType.CL, response.getLeaveType());

        verify(leaveRepo, times(1)).save(any(com.example.leave_management.entity.LeaveRequest.class));
    }

    @Test
    @DisplayName("Scenario 2: Leave creation rejected when start date is after end date")
    void testCreateLeave_InvalidDateRange_ThrowsBadRequest() {
        request.setStartDate(LocalDate.of(2024, 9, 15));
        request.setEndDate(LocalDate.of(2024, 9, 10)); // start > end
        when(employeeService.findOrThrow(1L)).thenReturn(employee);

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                leaveService.createLeave(request)
        );

        assertTrue(ex.getMessage().contains("cannot be after end date"));
        verify(leaveRepo, never()).save(any());
    }

    @Test
    @DisplayName("Scenario 3: Leave creation rejected with invalid leave type (e.g. 'AL')")
    void testCreateLeave_InvalidLeaveType_ThrowsBadRequest() {
        request.setLeaveType("AL"); // Invalid
        when(employeeService.findOrThrow(1L)).thenReturn(employee);

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                leaveService.createLeave(request)
        );

        assertTrue(ex.getMessage().contains("Invalid leave type"));
        verify(leaveRepo, never()).save(any());
    }

    @Test
    @DisplayName("Scenario 4: Leave approval succeeds for PENDING leave")
    void testApproveLeave_Success() {
        when(leaveRepo.findById(10L)).thenReturn(Optional.of(leaveEntity));
        when(leaveRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        LeaveResponse response = leaveService.approveLeave(10L);

        assertNotNull(response);
        assertEquals(LeaveStatus.APPROVED, response.getStatus());
    }

    @Test
    @DisplayName("Scenario 5 (Business Rule): Approved leave CANNOT be rejected")
    void testRejectLeave_AlreadyApproved_ThrowsBadRequest() {
        leaveEntity.setStatus(LeaveStatus.APPROVED);
        when(leaveRepo.findById(10L)).thenReturn(Optional.of(leaveEntity));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                leaveService.rejectLeave(10L)
        );

        assertTrue(ex.getMessage().contains("Cannot reject an already APPROVED leave"));
        verify(leaveRepo, never()).save(any());
    }

    @Test
    @DisplayName("Scenario 6 (Business Rule): Rejected leave CANNOT be cancelled")
    void testCancelLeave_AlreadyRejected_ThrowsBadRequest() {
        leaveEntity.setStatus(LeaveStatus.REJECTED);
        when(leaveRepo.findById(10L)).thenReturn(Optional.of(leaveEntity));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                leaveService.cancelLeave(10L)
        );

        assertTrue(ex.getMessage().contains("Cannot cancel a REJECTED leave"));
        verify(leaveRepo, never()).save(any());
    }

    @Test
    @DisplayName("Scenario 7: Cancel succeeds on PENDING leave")
    void testCancelLeave_Success() {
        leaveEntity.setStatus(LeaveStatus.PENDING);
        when(leaveRepo.findById(10L)).thenReturn(Optional.of(leaveEntity));
        when(leaveRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        LeaveResponse response = leaveService.cancelLeave(10L);

        assertEquals(LeaveStatus.CANCELLED, response.getStatus());
    }

    @Test
    @DisplayName("Scenario 8 (Business Rule): Approved leave CANNOT be cancelled")
    void testCancelLeave_AlreadyApproved_ThrowsBadRequest() {
        leaveEntity.setStatus(LeaveStatus.APPROVED);
        when(leaveRepo.findById(10L)).thenReturn(Optional.of(leaveEntity));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                leaveService.cancelLeave(10L)
        );

        assertTrue(ex.getMessage().contains("Cannot cancel an already APPROVED leave"));
        verify(leaveRepo, never()).save(any());
    }
}
