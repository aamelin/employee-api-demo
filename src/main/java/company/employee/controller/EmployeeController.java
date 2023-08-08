package company.employee.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import company.employee.dto.EmployeeDto;
import company.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    List<EmployeeDto> getAllEmployees() {
        throw new UnsupportedOperationException("Not implmented");
    }

    @GetMapping("/{id}")
    EmployeeDto getEmployee(@PathVariable final UUID id) {
        throw new UnsupportedOperationException("Not implmented");
    }

    @PostMapping
    EmployeeDto createEmployee(@Valid @RequestBody final EmployeeDto employee) {
        log.info("Received: {}", employee);
        // employeeService.create(employee);
        return employee;
        // throw new UnsupportedOperationException("Not implmented");
    }

    @PutMapping("/{id}")
    EmployeeDto updateEmployee(@Valid @RequestBody final EmployeeDto employee) {
        throw new UnsupportedOperationException("Not implmented");
    }
}
