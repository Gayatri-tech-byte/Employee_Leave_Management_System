package com.example.leave_management.dto;

/**
 * Response body returned by all /api/employees endpoints.
 * A separate response DTO keeps the API contract stable even if the entity changes.
 */
public class EmployeeResponse {

    private Long id;
    private String name;
    private String email;
    private String department;
    private String role;

    // ──────────── Constructors ────────────

    public EmployeeResponse() {}

    public EmployeeResponse(Long id, String name, String email, String department, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.role = role;
    }

    // ──────────── Getters & Setters ────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
