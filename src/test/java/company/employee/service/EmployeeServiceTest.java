package company.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import company.employee.config.ModelMapperConfig;
import company.employee.domain.Employee;
import company.employee.dto.EmployeeDataDto;
import company.employee.dto.EmployeeDto;
import company.employee.repository.EmployeeRepository;
import company.employee.service.EventPublisherService.EventType;
import company.employee.util.EmployeeDataGenerator;
import company.employee.util.UuidSource;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    private final ModelMapper mapper = new ModelMapperConfig()
            .modelMapper();
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EventPublisherService eventPublisherService;

    @Mock
    private UuidSource uuidSource;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(
                mapper,
                uuidSource,
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

    @Test
    void testCreateEmployeePersistsEmployee() {
        // given
        EmployeeDataDto employeeData = EmployeeDataGenerator.createEmployeeDataDto();
        Employee savedEmployeeEntity = mapper.map(employeeData, Employee.class);
        UUID employeeId = UUID.randomUUID();
        savedEmployeeEntity.setEmployeeId(employeeId);
        EmployeeDto expectedEmployee = mapper.map(savedEmployeeEntity, EmployeeDto.class);
        // when
        when(uuidSource.randomUUID()).thenReturn(employeeId);
        when(employeeRepository.save(savedEmployeeEntity)).thenReturn(savedEmployeeEntity);
        Optional<EmployeeDto> result = employeeService.create(employeeData);

        // then
        assertThat(result)
                .isPresent()
                .contains(expectedEmployee);
        verify(eventPublisherService).publishEmployeeEvent(EventType.CREATED, expectedEmployee);
    }

    @Test
    void testEmployeeWithExistingEmailNotCreated() {
        // given
        EmployeeDataDto employeeData = EmployeeDataGenerator.createEmployeeDataDto();
        String employeeEmail = employeeData.getEmail();
        Employee savedEmployeeEntity = mapper.map(employeeData, Employee.class);
        UUID employeeId = UUID.randomUUID();
        savedEmployeeEntity.setEmployeeId(employeeId);
        // when
        when(employeeRepository.findByEmail(employeeEmail))
                .thenReturn(Optional.of(savedEmployeeEntity));
        Optional<EmployeeDto> result = employeeService.create(employeeData);

        // then
        assertThat(result).isEmpty();
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateEmployeeChangesEmployeeData() {
        // given
        UUID employeeId = UUID.randomUUID();

        EmployeeDataDto updatedEmployeeData = EmployeeDataGenerator.createEmployeeDataDto(); // generates new email
        updatedEmployeeData.setLastName("lastname" + Instant.now().getEpochSecond());
        updatedEmployeeData.setHobbies(Set.of("hobby1", "hobby2"));
        Employee updatedEmployeeEntity = mapper.map(updatedEmployeeData, Employee.class);
        updatedEmployeeEntity.setEmployeeId(employeeId);
        EmployeeDto updatedEmployeeDto = mapper.map(updatedEmployeeEntity, EmployeeDto.class);

        EmployeeDataDto originalEmployeeData = EmployeeDataGenerator.createEmployeeDataDto();
        Employee originalEmployeeEntity = mapper.map(originalEmployeeData, Employee.class);
        originalEmployeeEntity.setEmployeeId(employeeId);

        // when
        when(employeeRepository.findByEmployeeId(employeeId))
                .thenReturn(Optional.of(originalEmployeeEntity));
        when(employeeRepository.save(updatedEmployeeEntity)).thenReturn(updatedEmployeeEntity);
        Optional<EmployeeDto> result = employeeService.update(employeeId, updatedEmployeeData);

        // then
        assertThat(result)
                .isPresent()
                .contains(updatedEmployeeDto);
        verify(eventPublisherService).publishEmployeeEvent(EventType.UPDATED, updatedEmployeeDto);
    }

    @Test
    void testNoUpdateForUnknownEmployeeId() {
        // given
        UUID wrongEmployeeId = UUID.randomUUID();

        EmployeeDataDto updatedEmployeeData = EmployeeDataGenerator.createEmployeeDataDto(); // generates new email

        // when
        when(employeeRepository.findByEmployeeId(wrongEmployeeId))
                .thenReturn(Optional.empty());
        Optional<EmployeeDto> result = employeeService.update(wrongEmployeeId, updatedEmployeeData);

        // then
        assertThat(result).isEmpty();
        verify(eventPublisherService, never()).publishEmployeeEvent(eq(EventType.UPDATED), any(EmployeeDto.class));
    }

    @Test
    void testDeleteEmployeeDeletesEmployeeRecord() {
        // given
        EmployeeDataDto employeeData = EmployeeDataGenerator.createEmployeeDataDto();
        Employee savedEmployeeEntity = mapper.map(employeeData, Employee.class);
        UUID employeeId = UUID.randomUUID();
        savedEmployeeEntity.setEmployeeId(employeeId);
        EmployeeDto expectedEmployee = mapper.map(savedEmployeeEntity, EmployeeDto.class);
        // when
        // when(uuidSource.randomUUID()).thenReturn(employeeId);
        when(employeeRepository.findByEmployeeId(employeeId))
                .thenReturn(Optional.of(savedEmployeeEntity));
        Optional<EmployeeDto> result = employeeService.delete(employeeId);

        // then
        assertThat(result)
                .isPresent()
                .contains(expectedEmployee);
        verify(eventPublisherService).publishEmployeeEvent(EventType.DELETED, expectedEmployee);
    }

    @Test
    void testDeleteWithWrongEmployeeId() {
        // given
        UUID wrongId = UUID.randomUUID();
        // when
        when(employeeRepository.findByEmployeeId(wrongId))
                .thenReturn(Optional.empty());
        Optional<EmployeeDto> result = employeeService.delete(wrongId);

        // then
        assertThat(result).isEmpty();
        verify(eventPublisherService, never()).publishEmployeeEvent(eq(EventType.DELETED), any(EmployeeDto.class));
    }
}
