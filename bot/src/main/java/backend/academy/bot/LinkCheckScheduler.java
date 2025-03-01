package backend.academy.bot;

import backend.academy.bot.model.LinkUpdate;
import backend.academy.bot.service.LinkTrackerBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkCheckScheduler {

    private final RestTemplate restTemplate;
    private final LinkTrackerBot linkTrackerBot;

    @Value("${bot.base-url}")  // URL Scrapper API (из application.yml)
    private String botBaseUrl;

    @Scheduled(fixedRate = 30000)  // Проверяем обновления раз в минуту
    public void checkForUpdates() {
        log.info("Запрос обновлений от Scrapper...");

        try {
            String url = botBaseUrl + "/updates";

            // Подготовка заголовков и тела запроса
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Пустое тело запроса (или добавьте необходимые данные, если требуется)
            HttpEntity<String> request = new HttpEntity<>("{}", headers);

            // Отправка POST-запроса
            LinkUpdate[] updates = restTemplate.postForObject(url, request, LinkUpdate[].class);

            if (updates != null && updates.length > 0) {
                log.info("Получено {} обновлений", updates.length);
                sendUpdatesToUsers(Arrays.asList(updates));
            } else {
                log.info("Нет новых обновлений.");
            }
        } catch (Exception e) {
            log.error("Ошибка при запросе обновлений: {}", e.getMessage());
        }
    }

    private void sendUpdatesToUsers(List<LinkUpdate> updates) {
        for (LinkUpdate update : updates) {
            for (Long chatId : update.tgChatIds()) {
                String message = "🔔 Обновление по ссылке: " + update.url() + "\n" + update.description();
                linkTrackerBot.sendMessage(chatId, message);
            }
        }
    }
}
