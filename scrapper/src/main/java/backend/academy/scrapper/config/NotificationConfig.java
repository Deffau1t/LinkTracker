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

@Configuration
public class NotificationConfig {

    @Bean
    @ConditionalOnProperty(name = "app.message-transport", havingValue = "Kafka")
    public NotificationService kafkaMessagesNotificationService(
        KafkaTemplate<String, LinkUpdateDTO> kafkaTemplate,
        @Value("${app.kafka.topicNotifications}") String topic
    ) {
        return new KafkaNotificationService(kafkaTemplate, topic);
    }

    @Bean
    @ConditionalOnProperty(name = "app.message-transport", havingValue = "HTTP")
    public NotificationService httpMessagesNotificationService(
        RestTemplate restTemplate,
        @Value("${bot.base-url}") String botBaseUrl
    ) {
        return new HttpNotificationService(restTemplate, botBaseUrl);
    }

    @Bean
    public ProducerFactory<String, LinkUpdateDTO> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        JsonSerializer<LinkUpdateDTO> jsonSerializer = new JsonSerializer<>();
        jsonSerializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(configProps, new StringSerializer(), jsonSerializer);
    }

    @Bean
    public KafkaTemplate<String, LinkUpdateDTO> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
