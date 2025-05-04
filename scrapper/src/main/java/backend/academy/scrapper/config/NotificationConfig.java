package backend.academy.scrapper.config;

import backend.academy.scrapper.dto.LinkUpdateDTO;
import backend.academy.scrapper.service.notification.HttpNotificationService;
import backend.academy.scrapper.service.notification.KafkaNotificationService;
import backend.academy.scrapper.service.notification.NotificationService;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

/**
 * NotificationConfig - Конфигурация для определения
 * способа отправки уведомлений.
 */

@Configuration
public class NotificationConfig {
    /**
     * kafkaMessagesNotificationService - Метод для создания объекта,
     * который отправляет уведомления на Kafka.
     * @param kafkaTemplate - kafka-шаблон
     * @param topic - топик, на который будут отправляться уведомления
     * @return объект, реализующий интерфейс NotificationService
     */
    @Bean
    @ConditionalOnProperty(
        name = "app.message-transport",
        havingValue = "Kafka"
    )
    public NotificationService kafkaMessagesNotificationService(
        final KafkaTemplate<String, LinkUpdateDTO> kafkaTemplate,
        final @Value("${app.kafka.topicNotifications}") String topic
    ) {
        return new KafkaNotificationService(kafkaTemplate, topic);
    }

    /**
     * httpMessagesNotificationService - Метод для создания объекта,
     * который отправляет уведомления через HTTP.
     * @param restTemplate - объект для выполнения запросов
     * @param botBaseUrl - базовый адрес бота
     * @return объект, реализующий интерфейс NotificationService
     */
    @Bean
    @ConditionalOnProperty(
        name = "app.message-transport",
        havingValue = "HTTP"
    )
    public NotificationService httpMessagesNotificationService(
        final RestTemplate restTemplate,
        final @Value("${bot.base-url}") String botBaseUrl
    ) {
        return new HttpNotificationService(restTemplate, botBaseUrl);
    }

    /**
     * producerFactory - Метод для создания объекта,
     * который позволяет отправлять сообщения в Kafka.
     * @return объект для отправки сообщений в Kafka
     */
    @Bean
    public ProducerFactory<String, LinkUpdateDTO> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            "localhost:9092");

        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class);

        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            JsonSerializer.class);

        JsonSerializer<LinkUpdateDTO> jsonSerializer = new JsonSerializer<>();
        jsonSerializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
            configProps,
            new StringSerializer(),
            jsonSerializer
        );
    }

    /**
     * kafkaTemplate - Метод для создания kafka-шаблона
     * для отправки сообщений в Kafka.
     * @return объект, который позволяет отправлять сообщения в Kafka
     */
    @Bean
    public KafkaTemplate<String, LinkUpdateDTO> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
