package company.employee.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import company.employee.domain.Employee;
import company.employee.dto.EmployeeDataDto;
import company.employee.dto.EmployeeDto;
import company.employee.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper mapper;

    @Override
    public Optional<EmployeeDto> find(final UUID employeeId) {
        Optional<Employee> employee = employeeRepository.findByEmployeeId(employeeId);
        return employee.stream()
                .map(e -> mapper.map(e, EmployeeDto.class))
                .findAny();
    }

    @Override
    public List<EmployeeDto> findAll() {
        return employeeRepository.findAll()
                .stream()
                .map(e -> mapper.map(e, EmployeeDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EmployeeDto create(final EmployeeDataDto employeeDataDto) {
        final Employee employee = mapper.map(employeeDataDto, Employee.class);
        employee.setEmployeeId(UUID.randomUUID());

        final Employee savedEmployee = employeeRepository.save(employee);
        final EmployeeDto savedEmployeeDto = mapper.map(savedEmployee, EmployeeDto.class);
        return savedEmployeeDto;
    }

    @Transactional
    @Override
    public Optional<EmployeeDto> update(final UUID employeeId, final EmployeeDataDto employeeDataDto) {
        Optional<Employee> employee = employeeRepository.findByEmployeeId(employeeId);
        if (employee.isEmpty()) {
            log.info("updating a non-existent employee with id: {}", employeeId);
            return Optional.empty();
        } else {
            Employee persistedEmployee = employee.get();
            Employee updatedEmployee = mapper.map(employeeDataDto, Employee.class);
            updatedEmployee.setId(persistedEmployee.getId());
            updatedEmployee.setEmployeeId(persistedEmployee.getEmployeeId());

            Employee savedEmployee = employeeRepository.save(updatedEmployee);
            EmployeeDto updatedEmployeeDto = mapper.map(savedEmployee, EmployeeDto.class);
            return Optional.of(updatedEmployeeDto);
        }
    }

    @Transactional
    @Override
    public long delete(final UUID employeeId) {
        return employeeRepository.deleteByEmployeeId(employeeId);
    }

}
