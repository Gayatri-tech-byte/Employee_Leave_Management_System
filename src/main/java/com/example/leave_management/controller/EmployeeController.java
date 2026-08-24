package com.example.leave_management.controller;

import com.example.leave_management.dto.EmployeeRequest;
import com.example.leave_management.dto.EmployeeResponse;
import com.example.leave_management.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for employee CRUD.
 *
 * Endpoints:
 *   POST  /api/employees        → create employee
 *   GET   /api/employees        → list all employees
 *   GET   /api/employees/{id}   → get employee by id
 */
@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")   // allow the HTML frontend (same machine, different port) to call this API
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // ── POST /api/employees ───────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);  // 201 Created
    }

    // ── GET /api/employees ────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // ── GET /api/employees/{id} ───────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }
}
