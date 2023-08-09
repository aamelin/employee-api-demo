package company.employee.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import company.employee.domain.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    long deleteByEmployeeId(UUID employId);
}
