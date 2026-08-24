package com.example.leave_management.repository;

import com.example.leave_management.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Used to check duplicate emails before save
    Optional<Employee> findByEmail(String email);
}
