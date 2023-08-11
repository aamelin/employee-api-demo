package company.employee.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import company.employee.domain.Employee;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    long deleteByEmployeeId(@NotNull UUID employeeId);

    Optional<Employee> findByEmployeeId(@NotNull UUID employeeId);

    Optional<Employee> findByEmail(@Email String email);
}
