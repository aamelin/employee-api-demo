package company.employee.util;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import company.employee.domain.Employee;
import company.employee.domain.Hobby;
import company.employee.dto.EmployeeDataDto;
import company.employee.dto.EmployeeDto;
import net.datafaker.Faker;

public class EmployeeDataGenerator {
    private static final Faker faker = new Faker();

    public static EmployeeDataDto createEmployeeDataDto() {
        var dto = new EmployeeDataDto();
        dto.setFirstName(faker.name().firstName());
        dto.setLastName(faker.name().lastName());
        dto.setBirthday(birthday());
        dto.setEmail(faker.internet().emailAddress());
        dto.setHobbies(hobbies());
        return dto;
    }

    public static EmployeeDto createEmployeeDto() {
        var dto = new EmployeeDto();
        dto.setFirstName(faker.name().firstName());
        dto.setLastName(faker.name().lastName());
        dto.setBirthday(birthday());
        dto.setEmail(faker.internet().emailAddress());
        dto.setHobbies(hobbies());
        return dto;
    }

    public static Employee createEmployee() {
        var employee = new Employee();

        employee.setId(faker.number().randomNumber());
        employee.setEmployeeId(UUID.randomUUID());
        employee.setFirstName(faker.name().firstName());
        employee.setLastName(faker.name().lastName());
        employee.setBirthday(birthday());
        employee.setEmail(faker.internet().emailAddress());
        employee.setHobbies(hobbyEntites());

        return employee;
    }

    private static LocalDate birthday() {
        return faker.date()
                .birthday(21, 65)
                .toLocalDateTime()
                .toLocalDate();
    }

    private static Set<String> hobbies() {
        return IntStream.of(faker.number().randomDigit())
                .mapToObj(i -> faker.text().text(2, 20))
                .collect(Collectors.toSet());
    }

    private static Set<Hobby> hobbyEntites() {
        return hobbies().stream()
                .map(hobby -> new Hobby(faker.number().randomNumber(), hobby))
                .collect(Collectors.toSet());
    }
}
