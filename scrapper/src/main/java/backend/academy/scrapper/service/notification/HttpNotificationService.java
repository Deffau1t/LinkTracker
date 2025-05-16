package backend.academy.scrapper.service.notification;

import backend.academy.scrapper.dto.LinkUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

/**
 * HttpNotificationService - отправляет уведомления в Telegram бота
 * по протоколу HTTP.
 */

@Slf4j
public class HttpNotificationService implements NotificationService {
    /**
     * restTemplate - клиент для отправки запросов к Telegram боту.
     */
    private final RestTemplate restTemplate;
    /**
     * botUrl - URL Telegram бота.
     */
    private final String botUrl;

    /**
     * Конструктор класса HttpNotificationService.
     * @param template - клиент для отправки запросов к Telegram боту.
     * @param url - URL Telegram бота.
     */
    public HttpNotificationService(
        final RestTemplate template,
        final @Value("${bot.base-url}") String url
    ) {
        this.restTemplate = template;
        this.botUrl = url;
    }

    /**
     * Метод для отправки уведомления в Telegram бота по протоколу HTTP.
     * @param update - данные о состоянии ссылки.
     */
    @Override
    public void sendNotification(final LinkUpdateDTO update) {
        restTemplate.postForEntity(botUrl + "/updates", update, Void.class);
    }
}
