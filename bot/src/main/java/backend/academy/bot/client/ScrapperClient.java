package backend.academy.bot.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * ScrapperClient
 * Общей клиент для взаимодействия с Scrapper.
 */

@Service
@Slf4j
public class ScrapperClient {

    private final RestTemplate restTemplate;

    @Value("${scrapper.base-url}")
    private String scrapperBaseUrl;

    @Autowired
    public ScrapperClient(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * trackLink - Отправляет запрос на добавление ссылки в scrapper.
     * @param chatId - ID чата в Telegram.
     * @param link - Ссылка для отслеживания.
     * @param tags - Теги для отслеживания.
     * @param filters - Фильтры для отслеживания.
     */

    public void trackLink(final Long chatId, final String link,
                          final String tags, final String filters) {
        registerChatIfNeeded(chatId);

        String url = scrapperBaseUrl + "/links";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Tg-Chat-Id", chatId.toString());

        String requestBody = String.format(
            "{\"link\":\"%s\", \"tags\":[\"%s\"], \"filters\":[\"%s\"]}",
            link, tags, filters
        );

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                request,
                String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Ссылка успешно добавлена в scrapper!");
            } else {
                log.error("Ошибка сервера: {} - {}",
                    response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке запроса: {}", e.getMessage());
        }
    }

    /**
     * registerChatIfNeeded - Регистрирует чат в scrapper, если он еще не зарегистрирован.
     * @param chatId - ID чата в Telegram.
     */

    private void registerChatIfNeeded(Long chatId) {
        String url = scrapperBaseUrl + "/tg-chat/" + chatId;

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                null,
                String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Чат {} зарегистрирован в Scrapper", chatId);
            } else {
                log.warn(
                    "Чат {} уже зарегистрирован или произошла ошибка",
                    chatId);
            }
        } catch (Exception e) {
            log.error("Ошибка при регистрации чата: {}", e.getMessage());
        }
    }


    /**
     * untrackLink - Отправляет запрос на удаление ссылки из scrapper.
     * @param chatId - ID чата в Telegram.
     * @param link - Ссылка для удаления.
     */

    public void untrackLink(final Long chatId, final String link) {
        String url = scrapperBaseUrl + "/links";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Tg-Chat-Id", chatId.toString());

        HttpEntity<String> request =
            new HttpEntity<>(String.format("{\"link\":\"%s\"}", link), headers);

        log.info("Отправка запроса на удаление: {}", url);
        log.info("Заголовки: {}", headers);
        log.info("Тело запроса: {}", request.getBody());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.DELETE,
                request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Ссылка успешно удалена в scrapper.");
            } else {
                log.error(
                    "Ошибка при удалении: {} - {}",
                    response.getStatusCode(),
                    response.getBody()
                );
            }
        } catch (Exception e) {
            log.error(
                "Ошибка при отправке запроса в scrapper: {}",
                e.getMessage()
            );
        }
    }

}
