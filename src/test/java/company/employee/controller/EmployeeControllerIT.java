package company.employee.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import company.employee.dto.EmployeeDataDto;
import company.employee.dto.EmployeeDto;
import company.employee.util.EmployeeDataGenerator;
import company.employee.util.EventPublisher.EventType;

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(topics = "${topics.employee-events}", partitions = 1, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class EmployeeControllerIT {
    private static final String EMPLOYEES_ENDPOINT = "/employees";
    private static final String EMPLOYEES_ID_ENDPOINT = EMPLOYEES_ENDPOINT + "/%s";
    private static final String EVENT_TYPE_KEY = "event_type";
    private static final String EVENT_DATA_KEY = "employee_data";

    @Value("${app.http.api-key-header}")
    private String apiKeyHeader;

    @Value("${app.http.api-key}")
    private String apiKey;

    @Value("${topics.employee-events}")
    private String topic;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;

    private Consumer<String, String> consumer;

    @BeforeEach
    public void setUpKafka() {
        if (consumer == null) {
            consumer = configureKafkaConsumer(kafkaBroker, topic);
        }
    }

    @Test
    void testEmployeeRetrieval() throws Exception {
        EmployeeDataDto employeeDataDto = EmployeeDataGenerator.createEmployeeDataDto();

        final String createEmployeeResponse = mockMvc.perform(post(EMPLOYEES_ENDPOINT)
                .contentType(APPLICATION_JSON)
                .header(apiKeyHeader, apiKey)
                .content(objectMapper.writeValueAsString(employeeDataDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        EmployeeDto employeeCreatedDto = objectMapper.readValue(createEmployeeResponse, EmployeeDto.class);
        verifyEmployeeDto(employeeCreatedDto, employeeDataDto);

        final UUID employeeId = employeeCreatedDto.getEmployeeId();
        final String employeeRetrievedByIdResponse = mockMvc
                .perform(get(String.format(EMPLOYEES_ID_ENDPOINT, employeeId))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        EmployeeDto employeeDtoFromGetEmployeeByIdResponse = objectMapper.readValue(
                employeeRetrievedByIdResponse,
                EmployeeDto.class);
        verifyEmployeeDto(employeeDtoFromGetEmployeeByIdResponse, employeeDataDto);

        final String allEmployeesResponse = mockMvc.perform(get(EMPLOYEES_ENDPOINT)
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        EmployeeDto[] employeeDtoArray = objectMapper.readValue(allEmployeesResponse, EmployeeDto[].class);
        assertThat(employeeDtoArray)
                .isNotNull()
                .hasSize(1);
        EmployeeDto employeeDtoFromGetAllResponse = employeeDtoArray[0];
        verifyEmployeeDto(employeeDtoFromGetAllResponse, employeeDataDto);

        List<Map<EventType, EmployeeDto>> singleRecord = readKafkaRecords();
        assertThat(singleRecord)
                .containsExactly(Map.of(EventType.CREATED, employeeDtoFromGetAllResponse));
    }

    @Test
    void testEmployeeUpdate() throws Exception {
        EmployeeDataDto employeeDataDto = EmployeeDataGenerator.createEmployeeDataDto();

        final String createEmployeeResponse = mockMvc.perform(post(EMPLOYEES_ENDPOINT)
                .contentType(APPLICATION_JSON)
                .header(apiKeyHeader, apiKey)
                .content(objectMapper.writeValueAsString(employeeDataDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        EmployeeDto employeeCreatedDto = objectMapper.readValue(createEmployeeResponse, EmployeeDto.class);
        verifyEmployeeDto(employeeCreatedDto, employeeDataDto);

        final UUID employeeId = employeeCreatedDto.getEmployeeId();
        final EmployeeDataDto updatedEmployeeDataDto = EmployeeDataGenerator.createEmployeeDataDto();
        final String updatedEmployeeResponse = mockMvc.perform(put(String.format(EMPLOYEES_ID_ENDPOINT, employeeId))
                .contentType(APPLICATION_JSON)
                .header(apiKeyHeader, apiKey)
                .content(objectMapper.writeValueAsString(updatedEmployeeDataDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        EmployeeDto updatedEmployeeDto = objectMapper.readValue(updatedEmployeeResponse, EmployeeDto.class);
        verifyEmployeeDto(updatedEmployeeDto, updatedEmployeeDataDto);

        List<Map<EventType, EmployeeDto>> employeeDtosFromEvents = readKafkaRecords();
        assertThat(employeeDtosFromEvents)
                .containsExactly(
                        Map.of(EventType.CREATED, employeeCreatedDto),
                        Map.of(EventType.UPDATED, updatedEmployeeDto));
    }

    @Test
    void testEmployeeDelete() throws Exception {
        EmployeeDataDto employeeDataDto = EmployeeDataGenerator.createEmployeeDataDto();

        final String createEmployeeResponse = mockMvc.perform(post(EMPLOYEES_ENDPOINT)
                .contentType(APPLICATION_JSON)
                .header(apiKeyHeader, apiKey)
                .content(objectMapper.writeValueAsString(employeeDataDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        EmployeeDto createdEmployeeDto = objectMapper.readValue(createEmployeeResponse, EmployeeDto.class);
        verifyEmployeeDto(createdEmployeeDto, employeeDataDto);

        final UUID employeeId = createdEmployeeDto.getEmployeeId();
        mockMvc.perform(delete(String.format(EMPLOYEES_ID_ENDPOINT, employeeId))
                .contentType(APPLICATION_JSON)
                .header(apiKeyHeader, apiKey))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Map<EventType, EmployeeDto>> employeeDtosFromEvents = readKafkaRecords();
        assertThat(employeeDtosFromEvents)
                .containsExactly(
                        Map.of(EventType.CREATED, createdEmployeeDto),
                        Map.of(EventType.DELETED, createdEmployeeDto));
    }

    @Test
    void testUnauthorisedAccessToCreateEmployeeEndpoint() throws Exception {
        EmployeeDataDto employeeDataDto = EmployeeDataGenerator.createEmployeeDataDto();

        mockMvc.perform(post(EMPLOYEES_ENDPOINT)
                .contentType(APPLICATION_JSON)
                .header(apiKeyHeader, "wrong-key")
                .content(objectMapper.writeValueAsString(employeeDataDto)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string(""));
        assertThat(readKafkaRecords())
                .isEmpty();
    }

    public Consumer<String, String> configureKafkaConsumer(final EmbeddedKafkaBroker broker, final String topicName) {
        final Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", broker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        final Consumer<String, String> consumer = new DefaultKafkaConsumerFactory<>(consumerProps,
                new StringDeserializer(), new StringDeserializer())
                .createConsumer();
        consumer.subscribe(Collections.singleton(topicName));
        return consumer;
    }

    public List<Map<EventType, EmployeeDto>> readKafkaRecords() {
        Iterable<ConsumerRecord<String, String>> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(2))
                .records(topic);
        return StreamSupport.stream(records.spliterator(), false)
                .map(record -> extractEmployeeDtoFromRecord(record))
                .collect(Collectors.toList());
    }

    public Map<EventType, EmployeeDto> extractEmployeeDtoFromRecord(final ConsumerRecord<String, String> record) {
        Map<String, Object> message;
        try {
            message = objectMapper.readValue(record.value(), new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("error mapping employee event", e);
        }

        EmployeeDto employeeDto = objectMapper.convertValue(message.get(EVENT_DATA_KEY), EmployeeDto.class);
        EventType eventType = objectMapper.convertValue(message.get(EVENT_TYPE_KEY), EventType.class);
        return Map.of(eventType, employeeDto);
    }

    public void verifyEmployeeDto(final EmployeeDto actual, final EmployeeDataDto expected) {
        assertThat(actual)
                .extracting(
                        EmployeeDto::getFirstName,
                        EmployeeDto::getLastName,
                        EmployeeDto::getBirthday,
                        EmployeeDto::getEmail)
                .contains(
                        expected.getFirstName(),
                        expected.getLastName(),
                        expected.getBirthday(),
                        expected.getEmail());
        assertThat(actual.getHobbies())
                .containsAll(expected.getHobbies());
    }
}
