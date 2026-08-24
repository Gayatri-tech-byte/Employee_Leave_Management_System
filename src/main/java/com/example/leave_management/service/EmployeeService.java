package com.example.leave_management.service;

import com.example.leave_management.dto.EmployeeRequest;
import com.example.leave_management.dto.EmployeeResponse;
import com.example.leave_management.entity.Employee;
import com.example.leave_management.entity.Role;
import com.example.leave_management.exception.BadRequestException;
import com.example.leave_management.exception.ResourceNotFoundException;
import com.example.leave_management.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // Constructor injection (no @Autowired needed — single constructor)
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // ── Create Employee ───────────────────────────────────────────────────────
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        // Business rule: name cannot be blank (also enforced by @NotBlank, but belt + suspenders)
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Employee name cannot be blank");
        }

        // Prevent duplicate email
        employeeRepository.findByEmail(request.getEmail()).ifPresent(e -> {
            throw new BadRequestException("An employee with email '" + request.getEmail() + "' already exists");
        });

        Role role = Role.EMPLOYEE;
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                role = Role.valueOf(request.getRole().trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Invalid role: '" + request.getRole() + "'. Allowed roles are: ADMIN, EMPLOYEE");
            }
        }

        Employee employee = new Employee(
                request.getName().trim(),
                request.getEmail().trim().toLowerCase(),
                request.getDepartment().trim(),
                role
        );

        Employee saved = employeeRepository.save(employee);
        return toResponse(saved);
    }

    // ── Get All Employees ─────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Get Employee by ID ────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = findOrThrow(id);
        return toResponse(employee);
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    /**
     * Fetches the Employee entity or throws 404. Used internally and by LeaveService.
     */
    public Employee findOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    private EmployeeResponse toResponse(Employee e) {
        return new EmployeeResponse(
                e.getId(),
                e.getName(),
                e.getEmail(),
                e.getDepartment(),
                e.getRole() != null ? e.getRole().name() : Role.EMPLOYEE.name()
        );
    }
}
