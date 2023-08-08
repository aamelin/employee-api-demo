package company.employee.domain;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue
    private Long id;

    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID employeeId;

    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotNull
    @Email
    private String email;
    @NotNull
    @Past
    private LocalDate birthDay;

    @OneToMany(mappedBy = "employee")
    private Set<Hobby> hobbies;
}
