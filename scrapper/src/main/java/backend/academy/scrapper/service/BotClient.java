package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * BotClient - Класс для взаимодействия с ботом.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class BotClient {

    /**
     * restTemplate - RestTemplate для отправки HTTP запросов.
     */
    @Autowired
    private final RestTemplate restTemplate;

    /**
     * botBaseUrl - Базовый URL для взаимодействия с ботом.
     */
    @Value("${bot.base-url}")
    private String botBaseUrl;

    /**
     * sendUpdate - Метод для отправки обновления в бот.
     * @param update - Обновление для отправки.
     */
    public void sendUpdate(final LinkUpdate update) {
        String url = botBaseUrl + "/updates";

        try {
            log.info("Отправка обновления в bot: {}", update);
            ResponseEntity<String> response = restTemplate.postForEntity(
                url, update, String.class
            );
            log.info(
                "Ответ от bot: {} - {}",
                response.getStatusCode(),
                response.getBody()
            );
        } catch (Exception e) {
            log.error(
                "Ошибка при отправке обновления в bot: {}",
                e.getMessage()
            );
        }
    }
}
