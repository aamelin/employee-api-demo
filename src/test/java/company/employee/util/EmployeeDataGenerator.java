package company.employee.util;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import company.employee.domain.Employee;
import company.employee.domain.Hobby;
import company.employee.dto.EmployeeDataDto;
import company.employee.dto.EmployeeDto;

public class EmployeeDataGenerator {

    public static EmployeeDataDto createEmployeeDataDto() {
        var employeeDataDto = new EmployeeDataDto();
        employeeDataDto.setFirstName("John");
        employeeDataDto.setLastName("Doe");
        employeeDataDto.setBirthday(LocalDate.of(1970, 2, 15));
        employeeDataDto.setEmail(String.format("john-doe_%s@example.com", Instant.now().getEpochSecond()));
        employeeDataDto.setHobbies(Set.of("chess", "basketball"));
        return employeeDataDto;
    }

    public static EmployeeDto createEmployeeDto() {
        var employeeDataDto = new EmployeeDto();
        employeeDataDto.setFirstName("John");
        employeeDataDto.setLastName("Doe");
        employeeDataDto.setBirthday(LocalDate.of(1970, 2, 15));
        employeeDataDto.setEmail(String.format("john-doe_%s@example.com", Instant.now().getEpochSecond()));
        employeeDataDto.setHobbies(Set.of("chess", "basketball"));
        return employeeDataDto;
    }

    public static Employee createEmployee() {
        var employee = new Employee();

        employee.setId(156L);
        employee.setEmployeeId(UUID.randomUUID());
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setBirthday(LocalDate.of(1970, 2, 15));
        employee.setEmail(String.format("john-doe_%s@example.com", Instant.now().getEpochSecond()));
        employee.setHobbies(Set.of(
                new Hobby(1L, "chess"),
                new Hobby(2L, "footbal")));

        return employee;
    }
}
