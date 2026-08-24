package com.example.leave_management;

import com.example.leave_management.entity.*;
import com.example.leave_management.repository.EmployeeRepository;
import com.example.leave_management.repository.LeaveRequestRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public DataInitializer(EmployeeRepository employeeRepository, LeaveRequestRepository leaveRequestRepository) {
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Override
    public void run(String... args) {
        if (employeeRepository.count() >= 10) {
            return; // Data already exists
        }

        // Clean slate if fewer than 10 to ensure fresh, clean 10 employees
        leaveRequestRepository.deleteAll();
        employeeRepository.deleteAll();

        // 1. Create 10 Dummy Employees
        Employee e1 = new Employee("Sarah Connor (HR Admin)", "sarah.admin@example.com", "Human Resources", Role.ADMIN, "admin123");
        Employee e2 = new Employee("Alice Johnson", "alice.johnson@example.com", "Engineering", Role.EMPLOYEE, "password123");
        Employee e3 = new Employee("Bob Smith", "bob.smith@example.com", "Human Resources", Role.EMPLOYEE, "password123");
        Employee e4 = new Employee("Charlie Davis", "charlie.davis@example.com", "Finance", Role.EMPLOYEE, "password123");
        Employee e5 = new Employee("Diana Prince", "diana.prince@example.com", "Marketing", Role.EMPLOYEE, "password123");
        Employee e6 = new Employee("Evan Wright", "evan.wright@example.com", "Engineering", Role.EMPLOYEE, "password123");
        Employee e7 = new Employee("Fiona Gallagher", "fiona.gallagher@example.com", "Product", Role.EMPLOYEE, "password123");
        Employee e8 = new Employee("George Miller", "george.miller@example.com", "Operations", Role.EMPLOYEE, "password123");
        Employee e9 = new Employee("Hannah Abbott", "hannah.abbott@example.com", "Engineering", Role.EMPLOYEE, "password123");
        Employee e10 = new Employee("Ian Malcolm", "ian.malcolm@example.com", "Data Science", Role.EMPLOYEE, "password123");

        employeeRepository.saveAll(List.of(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10));

        // 2. Create Sample Leave Requests for rich dashboards & SQL insights
        // Alice: 7 days total (> 5 days)
        LeaveRequest l1 = new LeaveRequest(e2, LeaveType.CL, LocalDate.of(2024, 9, 1), LocalDate.of(2024, 9, 3), 3, LeaveStatus.APPROVED, "Family occasion");
        LeaveRequest l2 = new LeaveRequest(e2, LeaveType.EL, LocalDate.of(2024, 10, 10), LocalDate.of(2024, 10, 13), 4, LeaveStatus.APPROVED, "Annual family vacation");

        // Bob: 4 days total
        LeaveRequest l3 = new LeaveRequest(e3, LeaveType.SL, LocalDate.of(2024, 9, 5), LocalDate.of(2024, 9, 6), 2, LeaveStatus.APPROVED, "Medical checkup");
        LeaveRequest l4 = new LeaveRequest(e3, LeaveType.CL, LocalDate.of(2024, 9, 20), LocalDate.of(2024, 9, 21), 2, LeaveStatus.PENDING, "Personal emergency");

        // Charlie: 2 days (Pending)
        LeaveRequest l5 = new LeaveRequest(e4, LeaveType.CL, LocalDate.of(2024, 9, 15), LocalDate.of(2024, 9, 16), 2, LeaveStatus.PENDING, "Attending wedding ceremony");

        // Diana: 1 day (Rejected)
        LeaveRequest l6 = new LeaveRequest(e5, LeaveType.EL, LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 1), 1, LeaveStatus.REJECTED, "Project critical deadline overlap");

        // Fiona: 6 days total (> 5 days)
        LeaveRequest l7 = new LeaveRequest(e7, LeaveType.EL, LocalDate.of(2024, 11, 4), LocalDate.of(2024, 11, 9), 6, LeaveStatus.APPROVED, "Recharge leave");

        // George: 3 days (Pending)
        LeaveRequest l8 = new LeaveRequest(e8, LeaveType.SL, LocalDate.of(2024, 11, 12), LocalDate.of(2024, 11, 14), 3, LeaveStatus.PENDING, "Seasonal flu recovery");

        // Hannah: 4 days (Approved)
        LeaveRequest l9 = new LeaveRequest(e9, LeaveType.CL, LocalDate.of(2024, 11, 20), LocalDate.of(2024, 11, 23), 4, LeaveStatus.APPROVED, "Home renovation");

        // Note: Evan Wright (e6) and Ian Malcolm (e10) have 0 leaves -> Attendance Champions!

        leaveRequestRepository.saveAll(List.of(l1, l2, l3, l4, l5, l6, l7, l8, l9));
    }
}
