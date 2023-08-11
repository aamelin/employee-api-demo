package company.employee.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import company.employee.dto.EmployeeDataDto;
import company.employee.dto.EmployeeDto;

public interface EmployeeService {

    List<EmployeeDto> findAll();

    Optional<EmployeeDto> find(final UUID employeeId);

    Optional<EmployeeDto> create(final EmployeeDataDto employeeDto);

    Optional<EmployeeDto> update(final UUID employeeId, final EmployeeDataDto employeeDto);

    Optional<EmployeeDto> delete(final UUID employeeId);
}
