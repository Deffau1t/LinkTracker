package backend.academy.scrapper.client;

import backend.academy.scrapper.dto.LinkUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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
    public void sendUpdate(final LinkUpdateDTO update) {
        String url = botBaseUrl + "/updates";

        if (update == null || update.url() == null
            || update.tgChatIds() == null) {
            log.error("Invalid update data: {}", update);
            return;
        }

        try {
            LinkUpdateDTO dto = LinkUpdateDTO.builder()
                .id(update.id())
                .url(update.url())
                .description(update.description())
                .tags(update.tags())
                .filters(update.filters())
                .tgChatIds(update.tgChatIds())
                .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<LinkUpdateDTO> request = new HttpEntity<>(
                dto,
                headers
            );

            ResponseEntity<String> response = restTemplate.postForEntity(
                url, request, String.class
            );

            log.info("Ответ от бота: {}", response.getStatusCode());
        } catch (HttpClientErrorException e) {
            log.error(
                "HTTP ошибка {}: {}",
                e.getStatusCode(),
                e.getResponseBodyAsString()
            );
        } catch (Exception e) {
            log.error("Ошибка отправки: {}", e.getMessage());
        }
    }
}
