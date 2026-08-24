package com.example.leave_management.controller;

import com.example.leave_management.dto.LeaveRequest;
import com.example.leave_management.dto.LeaveResponse;
import com.example.leave_management.service.LeaveService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for leave-request operations.
 *
 * Endpoints (exactly as specified in the assignment):
 *   POST  /api/leaves              → create leave request
 *   GET   /api/leaves              → get all leave requests
 *   GET   /api/leaves/{id}         → get leave by id
 *   PUT   /api/leaves/{id}/approve → approve leave
 *   PUT   /api/leaves/{id}/reject  → reject leave
 *   PUT   /api/leaves/{id}/cancel  → cancel leave
 */
@RestController
@RequestMapping("/api/leaves")
@CrossOrigin(origins = "*")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    // ── POST /api/leaves ──────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<LeaveResponse> createLeave(@Valid @RequestBody LeaveRequest request) {
        LeaveResponse response = leaveService.createLeave(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── GET /api/leaves ───────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<LeaveResponse>> getAllLeaves() {
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    // ── GET /api/leaves/employee/{employeeId} ─────────────────────────────────
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveResponse>> getLeavesByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveService.getLeavesByEmployee(employeeId));
    }

    // ── GET /api/leaves/{id} ──────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<LeaveResponse> getLeaveById(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.getLeaveById(id));
    }

    // ── PUT /api/leaves/{id}/approve ──────────────────────────────────────────
    @PutMapping("/{id}/approve")
    public ResponseEntity<LeaveResponse> approveLeave(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.approveLeave(id));
    }

    // ── PUT /api/leaves/{id}/reject ───────────────────────────────────────────
    @PutMapping("/{id}/reject")
    public ResponseEntity<LeaveResponse> rejectLeave(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.rejectLeave(id));
    }

    // ── PUT /api/leaves/{id}/cancel ───────────────────────────────────────────
    @PutMapping("/{id}/cancel")
    public ResponseEntity<LeaveResponse> cancelLeave(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.cancelLeave(id));
    }
}
