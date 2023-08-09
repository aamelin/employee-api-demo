package company.employee.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeDto {
    @NotNull
    @org.hibernate.validator.constraints.UUID
    private UUID employeeId;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotNull
    @Email
    private String email;
    @Past
    @NotNull
    private LocalDate birthday;
    @NotNull
    private Set<String> hobbies;
}
