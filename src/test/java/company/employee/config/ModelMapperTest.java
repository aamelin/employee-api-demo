package company.employee.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import company.employee.domain.Employee;
import company.employee.domain.Hobby;
import company.employee.dto.EmployeeDataDto;
import company.employee.util.EmployeeDataGenerator;

public class ModelMapperTest {
    private ModelMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ModelMapperConfig().modelMapper();
    }

    @Test
    void testMappingSettingsCompleteness() {
        mapper.validate();
    }

    @Test
    void testEmployeeDataDtoToEmployeeMapping() {
        EmployeeDataDto employeeDataDto = EmployeeDataGenerator.createEmployeeDataDto();
        Employee employee = mapper.map(employeeDataDto, Employee.class);
        assertThat(employee).extracting(
                Employee::getId,
                Employee::getEmployeeId,
                Employee::getFirstName,
                Employee::getLastName,
                Employee::getBirthday,
                Employee::getEmail)
                .contains(
                        null,
                        null,
                        employeeDataDto.getFirstName(),
                        employeeDataDto.getLastName(),
                        employeeDataDto.getBirthday(),
                        employeeDataDto.getEmail());
        assertThat(employee.getHobbies())
                .extracting(Hobby::getHobby)
                .containsAll(employeeDataDto.getHobbies());
    }

    @Test
    void testEmployeeToEmployeeDataDtoMapping() {
        Employee employee = EmployeeDataGenerator.createEmployee();
        List<String> hobbies = employee.getHobbies()
                .stream()
                .map(Hobby::getHobby)
                .collect(Collectors.toList());
        EmployeeDataDto employeeDataDto = mapper.map(employee, EmployeeDataDto.class);

        mapper.validate();

        assertThat(employeeDataDto).extracting(
                EmployeeDataDto::getFirstName,
                EmployeeDataDto::getLastName,
                EmployeeDataDto::getBirthday,
                EmployeeDataDto::getEmail)
                .contains(
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getBirthday(),
                        employee.getEmail());
        assertThat(employeeDataDto.getHobbies())
                .containsAll(hobbies);
    }

    @Test
    void testMappingHobbiesEmptySetToEmployeeEntity() {
        EmployeeDataDto employeeDataDto = EmployeeDataGenerator.createEmployeeDataDto();
        employeeDataDto.setHobbies(Set.of());
        Employee employee = mapper.map(employeeDataDto, Employee.class);

        assertThat(employee.getHobbies()).isEmpty();

    }

    @Test
    void testMappingHobbiesEmptySetToEmployeeDataDto() {
        Employee employee = EmployeeDataGenerator.createEmployee();
        employee.setHobbies(Set.of());
        EmployeeDataDto employeeDataDto = mapper.map(employee, EmployeeDataDto.class);

        assertThat(employeeDataDto.getHobbies()).isEmpty();

    }

}
