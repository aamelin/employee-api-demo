package company.employee.util;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import company.employee.dto.EmployeeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {
    public static final String EVENT_TYPE_FIELD_NAME = "event_type";
    public static final String EVENT_DATA_FIELD_NAME = "employee_data";
    @Value("${topics.employee-events}")
    private String employeeTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishEmployeeEvent(final EventType eventType, final EmployeeDto employeeDto) {
        log.info("publish event: {} for {}", employeeDto);
        kafkaTemplate.send(employeeTopicName, createEvent(eventType, employeeDto));
    }

    private Map<String, ?> createEvent(final EventType eventType, final EmployeeDto employeeDto) {
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
