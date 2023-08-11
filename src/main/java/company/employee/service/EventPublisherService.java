package company.employee.service;

import java.util.Map;

import company.employee.dto.EmployeeDto;

public interface EventPublisherService {
    public static final String EVENT_TYPE_FIELD_NAME = "event_type";
    public static final String EVENT_DATA_FIELD_NAME = "employee_data";

    void publishEmployeeEvent(final EventType eventType, final EmployeeDto employeeDto);

    default Map<String, ?> createEvent(final EventType eventType, final EmployeeDto employeeDto) {
        return Map.of(
                EVENT_TYPE_FIELD_NAME, eventType,
                EVENT_DATA_FIELD_NAME, employeeDto);
    }

    public enum EventType {
        CREATED,
        DELETED,
        UPDATED
    }

}
