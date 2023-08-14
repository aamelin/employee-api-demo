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
import company.employee.service.EventPublisherService.EventType;
import company.employee.util.UuidSource;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final ModelMapper mapper;
    private final UuidSource uuidSource;
    private final EmployeeRepository employeeRepository;
    private final EventPublisherService eventPublisherService;

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

    @Override
    public Optional<EmployeeDto> create(final EmployeeDataDto employeeDataDto) {
        if (employeeRepository.findByEmail(employeeDataDto.getEmail()).isPresent()) {
            log.info("can't create employee, email already exists: {}", employeeDataDto);
            return Optional.empty();
        }
        final Employee employee = mapper.map(employeeDataDto, Employee.class);
        employee.setEmployeeId(uuidSource.randomUUID());

        final Employee savedEmployee = employeeRepository.save(employee);
        final EmployeeDto savedEmployeeDto = mapper.map(savedEmployee, EmployeeDto.class);

        eventPublisherService.publishEmployeeEvent(EventType.CREATED, savedEmployeeDto);
        return Optional.of(savedEmployeeDto);
    }

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
            eventPublisherService.publishEmployeeEvent(EventType.UPDATED, updatedEmployeeDto);
            return Optional.of(updatedEmployeeDto);
        }
    }

    @Override
    public Optional<EmployeeDto> delete(final UUID employeeId) {
        Optional<EmployeeDto> employeeSearchResult = find(employeeId);
        if (employeeSearchResult.isEmpty()) {
            log.info("deleting a non-existent employee with id: {}", employeeId);
            return Optional.empty();
        } else {
            employeeRepository.deleteByEmployeeId(employeeId);
            EmployeeDto deletedEmployeeDto = employeeSearchResult.get();
            eventPublisherService.publishEmployeeEvent(EventType.DELETED, deletedEmployeeDto);
            return Optional.of(deletedEmployeeDto);
        }
    }
}
