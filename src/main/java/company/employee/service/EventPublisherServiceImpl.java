package company.employee.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import company.employee.dto.EmployeeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisherServiceImpl implements EventPublisherService {
    @Value("${topics.employee-events}")
    private String employeeTopicName;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishEmployeeEvent(final EventType eventType, final EmployeeDto employeeDto) {
        log.info("publish event: {} for {}", employeeDto);
        kafkaTemplate.send(employeeTopicName, createEvent(eventType, employeeDto));
    }

}
