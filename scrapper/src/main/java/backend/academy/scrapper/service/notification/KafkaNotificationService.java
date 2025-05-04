package backend.academy.scrapper.service.notification;

import backend.academy.scrapper.dto.LinkUpdateDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * KafkaNotificationService - класс для отправки уведомлений на Kafka-топик.
 */

public class KafkaNotificationService implements NotificationService {
    /**
     * kafkaTemplate - шаблон для отправки сообщений на Kafka.
     */
    private final KafkaTemplate<String, LinkUpdateDTO> kafkaTemplate;
    /**
     * Topic - имя топика, на который будут отправляться уведомления.
     */
    private final String topic;

    /**
     * Конструктор класса KafkaNotificationService.
     * @param template - шаблон для отправки сообщений на Kafka.
     * @param topic - имя топика, на который будут отправляться уведомления.
     */
    public KafkaNotificationService(
        final KafkaTemplate<String, LinkUpdateDTO> template,
        final @Value("${app.kafka.topicNotifications}") String topic
    ) {
        this.kafkaTemplate = template;
        this.topic = topic;
    }

    /**
     * Метод для отправки уведомления на Kafka.
     * @param notification - уведомление.
     */
    @Override
    public void sendNotification(final LinkUpdateDTO notification) {
        kafkaTemplate.send(topic, notification);
    }
}
