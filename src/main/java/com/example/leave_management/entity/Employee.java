package com.example.leave_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Employee name cannot be blank")
    @Column(nullable = false)
    private String name;

    @Email(message = "Must be a valid email address")
    @NotBlank(message = "Email cannot be blank")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Department cannot be blank")
    @Column(nullable = false)
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.EMPLOYEE;

    @Column(nullable = false)
    private String password = "password123";

    // ──────────── Constructors ────────────

    public Employee() {}

    public Employee(String name, String email, String department) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.role = Role.EMPLOYEE;
        this.password = "password123";
    }

    public Employee(String name, String email, String department, Role role) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.role = role != null ? role : Role.EMPLOYEE;
        this.password = "password123";
    }

    public Employee(String name, String email, String department, Role role, String password) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.role = role != null ? role : Role.EMPLOYEE;
        this.password = (password != null && !password.isBlank()) ? password : "password123";
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

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
