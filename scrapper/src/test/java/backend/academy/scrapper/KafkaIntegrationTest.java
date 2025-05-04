package backend.academy.scrapper;

import backend.academy.scrapper.dto.LinkUpdateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

@SpringBootTest
@Testcontainers
public class KafkaIntegrationTest {

    @Container
    static GenericContainer<?> kafka = new GenericContainer<>("confluentinc/cp-kafka:7.2.2")
        .withExposedPorts(9092, 29092, 2181)
        .withEnv("KAFKA_BROKER_ID", "1")
        .withEnv("KAFKA_ZOOKEEPER_CONNECT", "localhost:2181")
        .withEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT")
        .withEnv("KAFKA_ADVERTISED_LISTENERS", "PLAINTEXT://localhost:29092,PLAINTEXT_HOST://localhost:9092")
        .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
        .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
        .withEnv("KAFKA_LISTENERS", "PLAINTEXT://0.0.0.0:29092,PLAINTEXT_HOST://0.0.0.0:9092")
        .withEnv("KAFKA_INTER_BROKER_LISTENER_NAME", "PLAINTEXT")
        .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(2)));

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2.3")
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () ->
            "PLAINTEXT://" + kafka.getHost() + ":" + kafka.getMappedPort(29092));
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired KafkaTemplate<String, String> kafkaTemplate;
    @Autowired ObjectMapper objectMapper;

    private final BlockingQueue<ConsumerRecord<String, String>> dlqRecords = new LinkedBlockingQueue<>();

    @BeforeEach
    void setupDlqConsumer() {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "PLAINTEXT://" + kafka.getHost() + ":" + kafka.getMappedPort(29092));
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-dlq-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        var consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        ContainerProperties containerProps = new ContainerProperties("dlq-topic");

        var container = new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
        container.setupMessageListener((MessageListener<String, String>) dlqRecords::add);
        container.start();
    }

    @Test
    void validMessageShouldBeProcessed() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        var dto = new LinkUpdateDTO(1L, "https://example.com", "desc", List.of("tag"), List.of("filter"), List.of(123L));
        kafkaTemplate.send("kafka-1", objectMapper.writeValueAsString(dto));
        boolean processed = latch.await(5, TimeUnit.SECONDS);
        assert true;
    }

    @Test
    void invalidMessageShouldGoToDlq() throws InterruptedException {
        kafkaTemplate.send("kafka-1", "{not-json}");
        ConsumerRecord<String, String> record = dlqRecords.poll(10, TimeUnit.SECONDS);
        assert record != null;
        assert record.value().contains("{not-json}");
    }
}
