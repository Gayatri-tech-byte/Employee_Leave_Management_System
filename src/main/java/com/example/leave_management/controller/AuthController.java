package com.example.leave_management.controller;

import com.example.leave_management.dto.AuthResponse;
import com.example.leave_management.dto.LoginRequest;
import com.example.leave_management.entity.Employee;
import com.example.leave_management.entity.Role;
import com.example.leave_management.exception.ResourceNotFoundException;
import com.example.leave_management.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Authentication REST Controller for Role-Based Portals.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final EmployeeRepository employeeRepository;

    public AuthController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String password = request.getPassword().trim();

        // 1. Default Super Admin / HR credentials check
        if (email.equals("admin@leavehub.com") || email.equals("hr@leavehub.com") || email.equals("admin@example.com")) {
            // Allow admin passwords like "admin", "admin123", "hr123", "password123"
            if (!password.equals("admin123") && !password.equals("admin") && !password.equals("hr123") && !password.equals("password123")) {
                throw new com.example.leave_management.exception.BadRequestException("Invalid password for administrator account.");
            }
            return ResponseEntity.ok(new AuthResponse(
                    0L,
                    "HR Administrator",
                    email,
                    "Human Resources",
                    "ADMIN",
                    "Welcome Admin! You have full management access."
            ));
        }

        // 2. Find by Email in Database
        Optional<Employee> empOpt = employeeRepository.findByEmail(email);
        if (empOpt.isEmpty()) {
            throw new ResourceNotFoundException("No account found for email: '" + email + "'. Please check your credentials.");
        }

        Employee emp = empOpt.get();

        // Check password (if employee has password set, verify; or allow default "password123" / "admin123")
        if (emp.getPassword() != null && !emp.getPassword().isBlank()) {
            if (!emp.getPassword().equals(password) && !password.equals("password123") && !password.equals("admin123")) {
                throw new com.example.leave_management.exception.BadRequestException("Incorrect password for " + emp.getEmail());
            }
        }

        String roleStr = emp.getRole() != null ? emp.getRole().name() : Role.EMPLOYEE.name();

        return ResponseEntity.ok(new AuthResponse(
                emp.getId(),
                emp.getName(),
                emp.getEmail(),
                emp.getDepartment(),
                roleStr,
                "Authentication successful. Welcome " + emp.getName() + "!"
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<List<AuthResponse>> getAvailableUsers() {
        List<AuthResponse> list = employeeRepository.findAll()
                .stream()
                .map(e -> new AuthResponse(
                        e.getId(),
                        e.getName(),
                        e.getEmail(),
                        e.getDepartment(),
                        e.getRole() != null ? e.getRole().name() : Role.EMPLOYEE.name(),
                        null
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }
}
