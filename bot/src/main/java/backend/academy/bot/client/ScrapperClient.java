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

@Service
@Slf4j
public class ScrapperClient {

    private final RestTemplate restTemplate;

    @Value("${scrapper.base-url}")
    private String scrapperBaseUrl;

    @Autowired
    public ScrapperClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void trackLink(Long chatId, String link, String tags, String filters) {
        registerChatIfNeeded(chatId);

        String url = scrapperBaseUrl + "/links";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Tg-Chat-Id", chatId.toString());

        // Создаем корректное тело запроса
        String requestBody = String.format(
            "{\"link\":\"%s\", \"tags\":[\"%s\"], \"filters\":[\"%s\"]}",
            link, tags, filters
        );

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        log.info("Отправка запроса на URL: {}", url);
        log.info("Заголовки: {}", headers);
        log.info("Тело запроса: {}", requestBody);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Ссылка успешно добавлена в scrapper!");
            } else {
                log.error("Ошибка сервера: {} - {}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке запроса: {}", e.getMessage());
        }
    }


    private void registerChatIfNeeded(Long chatId) {
        String url = scrapperBaseUrl + "/tg-chat/" + chatId;

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Чат {} зарегистрирован в Scrapper", chatId);
            } else {
                log.warn("Чат {} уже зарегистрирован или произошла ошибка", chatId);
            }
        } catch (Exception e) {
            log.error("Ошибка при регистрации чата: {}", e.getMessage());
        }
    }


    public void untrackLink(Long chatId, String link) {
        String url = scrapperBaseUrl + "/links";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Tg-Chat-Id", chatId.toString());

        HttpEntity<String> request = new HttpEntity<>(String.format("{\"link\":\"%s\"}", link), headers);

        log.info("Отправка запроса на удаление: {}", url);
        log.info("Заголовки: {}", headers);
        log.info("Тело запроса: {}", request.getBody());

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.DELETE, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Ссылка успешно удалена в scrapper.");
            } else {
                log.error("Ошибка при удалении: {} - {}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке запроса в scrapper: {}", e.getMessage());
        }
    }

}
