package company.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import company.employee.config.ModelMapperConfig;
import company.employee.domain.Employee;
import company.employee.dto.EmployeeDto;
import company.employee.repository.EmployeeRepository;
import company.employee.util.EmployeeDataGenerator;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    private final ModelMapper mapper = new ModelMapperConfig()
            .modelMapper();
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EventPublisherService eventPublisherService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(
                mapper,
                employeeRepository,
                eventPublisherService);
    }

    @Test
    void testFindEmployeesReturnsAllAvailableEmployees() {
        // given
        EmployeeDto firstExpectedEmployee = EmployeeDataGenerator.createEmployeeDto();
        EmployeeDto secondExpectedEmployee = EmployeeDataGenerator.createEmployeeDto();
        Employee firstEmployee = mapper.map(firstExpectedEmployee, Employee.class);
        Employee secondEmployee = mapper.map(secondExpectedEmployee, Employee.class);
        // when
        when(employeeRepository.findAll()).thenReturn(
                List.of(firstEmployee, secondEmployee));
        List<EmployeeDto> employees = employeeService.findAll();

        // then
        assertThat(employees)
                .containsExactly(firstExpectedEmployee, secondExpectedEmployee);
    }

    @Test
    void testFindEmployeesReturnsEmptyListIfNoEmployeesAvailable() {
        // when
        when(employeeRepository.findAll()).thenReturn(List.of());
        List<EmployeeDto> employees = employeeService.findAll();

        // then
        assertThat(employees).isEmpty();
    }

    @Test
    void testFindEmployeesByUuidReturnsEmployee() {
        // given
        Employee expectedEmployee = EmployeeDataGenerator.createEmployee();
        UUID employeeUuid = expectedEmployee.getEmployeeId();
        EmployeeDto expectedEmployeeEntity = mapper.map(expectedEmployee, EmployeeDto.class);

        // when
        when(employeeRepository.findByEmployeeId(employeeUuid))
                .thenReturn(Optional.of(expectedEmployee));
        Optional<EmployeeDto> result = employeeService.find(employeeUuid);

        // then
        assertThat(result).isPresent();
        EmployeeDto actualEmployeeDto = result.get();
        assertThat(actualEmployeeDto).isEqualTo(expectedEmployeeEntity);
    }

    @Test
    void testFindEmployeesByUuidReturnsEmptyResponseForWrongEmployeeId() {
        // given
        UUID wrongUuid = UUID.randomUUID();

        // when
        when(employeeRepository.findByEmployeeId(wrongUuid))
                .thenReturn(Optional.empty());
        Optional<EmployeeDto> result = employeeService.find(wrongUuid);

        // then
        assertThat(result).isEmpty();
    }
}
