package company.employee.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
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
    EmployeeDto createEmployee(@Valid @RequestBody final EmployeeDataDto employee) {
        EmployeeDto createdEmployee = employeeService.create(employee);
        log.info("created employee: {}", createdEmployee);
        return createdEmployee;
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
    ResponseEntity<Map<UUID, Long>> deleteEmployee(@PathVariable final UUID id) {
        final long count = employeeService.delete(id);
        HttpStatus status;
        if (count == 1) {
            status = HttpStatus.OK;
        } else if (count == 0) {
            status = HttpStatus.NOT_FOUND;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(status)
                .body(Map.of(id, count));
    }
}
