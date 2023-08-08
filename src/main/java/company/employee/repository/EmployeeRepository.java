package company.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import company.employee.domain.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
