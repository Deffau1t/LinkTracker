package backend.academy.scrapper.service.notification;

import backend.academy.scrapper.dto.LinkUpdateDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaNotificationService implements NotificationService {

    private final KafkaTemplate<String, LinkUpdateDTO> kafkaTemplate;
    private final String topic;

    public KafkaNotificationService(KafkaTemplate<String, LinkUpdateDTO> kafkaTemplate,
                                     @Value("${app.kafka.topic.notifications}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void sendNotification(LinkUpdateDTO notification) {
        kafkaTemplate.send(topic, notification);
    }
}
