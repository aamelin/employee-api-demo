package company.employee.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import company.employee.dto.EmployeeDataDto;
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
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    ResponseEntity<EmployeeDto> getEmployee(@PathVariable final UUID id) {
        Optional<EmployeeDto> employee = employeeService.find(id);
        if (employee.isEmpty()) {
            log.info("employee not found, id {}", id);
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(employee.get());
        }
    }

    @PostMapping
    ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody final EmployeeDataDto employee) {
        Optional<EmployeeDto> createdEmployee = employeeService.create(employee);
        if (createdEmployee.isEmpty()) {
            log.info("error creating employee: {}", employee);
            return ResponseEntity.badRequest().build();
        } else {
            log.info("created employee: {}", createdEmployee);
            return ResponseEntity.ok(createdEmployee.get());
        }
    }

    @PutMapping("/{id}")
    ResponseEntity<EmployeeDto> updateEmployee(
            @PathVariable final UUID id,
            @Valid @RequestBody final EmployeeDataDto employee) {
        Optional<EmployeeDto> updatedEmployee = employeeService.update(id, employee);
        if (updatedEmployee.isEmpty()) {
            log.info("employee not found, id {}", id);
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(updatedEmployee.get());
        }
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteEmployee(@PathVariable final UUID id) {
        Optional<EmployeeDto> deletedEmployee = employeeService.delete(id);
        if (deletedEmployee.isEmpty()) {
            log.info("employee not found, id {}", id);
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }
}
