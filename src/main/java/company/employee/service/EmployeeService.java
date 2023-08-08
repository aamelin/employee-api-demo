package company.employee.service;

import company.employee.domain.Employee;
import company.employee.dto.EmployeeDto;

public interface EmployeeService {

    Employee find(EmployeeDto employeeDto);

    Employee create(EmployeeDto employeeDto);

    Employee update(EmployeeDto employeeDto);

    Employee delete(EmployeeDto employeeDto);
}
