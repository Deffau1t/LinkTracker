package backend.academy.scrapper.config;

import backend.academy.scrapper.service.notification.HttpNotificationService;
import backend.academy.scrapper.service.notification.KafkaNotificationService;
import backend.academy.scrapper.service.notification.NotificationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

    @Bean
    @ConditionalOnProperty(name = "app.message-transport", havingValue = "Kafka")
    public NotificationService kafkaMessagesNotificationService(KafkaNotificationService kafkaNotificationService) {
        return kafkaNotificationService;
    }

    @Bean
    @ConditionalOnProperty(name = "app.message-transport", havingValue = "HTTP")
    public NotificationService httpMessagesNotificationService(HttpNotificationService httpNotificationService) {
        return httpNotificationService;
    }
}
