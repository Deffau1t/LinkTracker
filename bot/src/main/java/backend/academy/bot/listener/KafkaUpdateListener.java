package backend.academy.bot.listener;

import backend.academy.bot.dto.LinkUpdateDTO;
import backend.academy.bot.service.LinkTrackerBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaUpdateListener {

    private final LinkTrackerBot linkTrackerBot;

    @KafkaListener(
        topics = "${app.kafka.topicNotifications}",
        groupId = "bot-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(LinkUpdateDTO update) {
        log.info("Получено из Kafka: {}", update);

        if (update.tgChatIds() == null || update.tgChatIds().isEmpty()) {
            log.warn("Нет чатов для оповещения");
            return;
        }

        for (Long chatId : update.tgChatIds()) {
            linkTrackerBot.sendMessage(chatId, update.description());
        }
    }
}
