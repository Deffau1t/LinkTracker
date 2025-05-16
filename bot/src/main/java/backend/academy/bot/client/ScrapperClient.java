package backend.academy.bot.client;

import backend.academy.bot.model.LinkResponse;
import backend.academy.bot.model.ListLinksResponse;
import backend.academy.bot.model.TrackLinkRequest;
import backend.academy.bot.model.UntrackLinkRequest;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * httpClient - RestTemplate для отправки HTTP запросов.
     */
    private final RestTemplate httpClient;

    /**
     * scrapperBaseUrl - Базовый URL для взаимодействия с scrapper.
     */
    @Value("${scrapper.base-url}")
    private String scrapperBaseUrl;

    /**
     * Конструктор класса ScrapperClient.
     * @param restTemplate - RestTemplate для отправки HTTP запросов.
     */
    public ScrapperClient(final RestTemplate restTemplate) {
        this.httpClient = restTemplate;
    }

    /**
     * trackLink - Отправляет запрос на добавление ссылки в scrapper.
     * @param chatId - ID чата в Telegram.
     * @param link - Ссылка для отслеживания.
     * @param tags - Теги для отслеживания.
     * @param filters - Фильтры для отслеживания.
     * @return - true, если запрос успешно отправлен, иначе false.
     */

    public boolean trackLink(final Long chatId,
                          final String link,
                          final String tags,
                          final String filters) {

        String url = scrapperBaseUrl + "/links";
        HttpHeaders headers = createHeaders(chatId);

        TrackLinkRequest requestBody = new TrackLinkRequest(
            link,
            tags.isEmpty() ? List.of() : List.of(tags.split(" ")),
            filters.isEmpty() ? List.of() : List.of(filters.split(" "))
        );

        HttpEntity<TrackLinkRequest> request = new HttpEntity<>(
            requestBody,
            headers
        );

        try {
            ResponseEntity<String> response = httpClient.postForEntity(
                url,
                request,
                String.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Ссылка успешно добавлена в scrapper!");
                return true;
            } else {
                log.error(
                    "Ошибка сервера: {} - {}",
                    response.getStatusCode(),
                    response.getBody()
                );
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке запроса: {}", e.getMessage());
        }
        return false;
    }

    /**
     * getTrackedLinks - Отправляет запрос на получение списка
     * отслеживаемых ссылок.
     * @param chatId - ID чата в Telegram.
     * @return - Список отслеживаемых ссылок.
     */
    public List<LinkResponse> getTrackedLinks(final Long chatId) {
        String url = scrapperBaseUrl + "/links";
        HttpHeaders headers = createHeaders(chatId);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<ListLinksResponse> response = httpClient.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                request,
                ListLinksResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()
                && response.getBody() != null
            ) {
                log.info("Получены отслеживаемые ссылки для чата {}", chatId);
                return response.getBody().links();
            } else {
                log.warn("Не удалось получить ссылки для чата {}", chatId);
            }
        } catch (Exception e) {
            log.error("Ошибка при получении ссылок: {}", e.getMessage());
        }

        return List.of();
    }

    /**
     * registerChatIfNeeded - Регистрирует чат в scrapper,
     * если он еще не зарегистрирован.
     * @param chatId - ID чата в Telegram.
     */

    public void registerChatIfNeeded(final Long chatId) {
        String url = scrapperBaseUrl + "/tg-chat/" + chatId;

        try {
            ResponseEntity<String> response = httpClient.postForEntity(
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
     * @return - true, если запрос успешно отправлен, иначе false.
     */

    public boolean untrackLink(final Long chatId, final String link) {
        String url = scrapperBaseUrl + "/links";
        HttpHeaders headers = createHeaders(chatId);

        UntrackLinkRequest requestBody = new UntrackLinkRequest(link);
        HttpEntity<UntrackLinkRequest> request = new HttpEntity<>(
            requestBody,
            headers
        );

        try {
            ResponseEntity<String> response = httpClient.exchange(
                url,
                org.springframework.http.HttpMethod.DELETE,
                request,
                String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Ссылка успешно удалена в scrapper.");
                return true;
            } else {
                log.error(
                    "Ошибка при удалении: {} - {}",
                    response.getStatusCode(),
                    response.getBody()
                );
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении ссылки: {}", e.getMessage());
        }
        return false;
    }

    /**
     * createHeaders - Создает HTTP заголовки для запросов.
     * @param chatId - ID чата в Telegram.
     * @return - HTTP заголовки.
     */
    private HttpHeaders createHeaders(final Long chatId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Tg-Chat-Id", chatId.toString());
        return headers;
    }
}
