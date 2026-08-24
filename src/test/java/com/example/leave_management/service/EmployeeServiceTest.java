package com.example.leave_management.service;

import com.example.leave_management.dto.EmployeeRequest;
import com.example.leave_management.dto.EmployeeResponse;
import com.example.leave_management.entity.Employee;
import com.example.leave_management.entity.Role;
import com.example.leave_management.exception.BadRequestException;
import com.example.leave_management.exception.ResourceNotFoundException;
import com.example.leave_management.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private EmployeeRequest request;

    @BeforeEach
    void setUp() {
        employee = new Employee("Alice Johnson", "alice@example.com", "Engineering", Role.EMPLOYEE);
        employee.setId(1L);

        request = new EmployeeRequest();
        request.setName("Alice Johnson");
        request.setEmail("alice@example.com");
        request.setDepartment("Engineering");
        request.setRole("EMPLOYEE");
    }

    @Test
    @DisplayName("Scenario 1: Employee creation succeeds with valid data")
    void testCreateEmployee_Success() {
        when(employeeRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponse response = employeeService.createEmployee(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Alice Johnson", response.getName());
        assertEquals("alice@example.com", response.getEmail());
        assertEquals("Engineering", response.getDepartment());
        assertEquals("EMPLOYEE", response.getRole());

        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Scenario 2: Employee creation fails when name is blank")
    void testCreateEmployee_BlankName_ThrowsBadRequest() {
        request.setName("   ");

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                employeeService.createEmployee(request)
        );

        assertTrue(ex.getMessage().contains("name cannot be blank"));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Scenario 3: Employee creation fails when email already exists")
    void testCreateEmployee_DuplicateEmail_ThrowsBadRequest() {
        when(employeeRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(employee));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                employeeService.createEmployee(request)
        );

        assertTrue(ex.getMessage().contains("already exists"));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Scenario 4: Get Employee by ID succeeds when found")
    void testGetEmployeeById_Found() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResponse response = employeeService.getEmployeeById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Alice Johnson", response.getName());
    }

    @Test
    @DisplayName("Scenario 5: Get Employee by ID throws 404 when not found")
    void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                employeeService.getEmployeeById(999L)
        );
    }
}
