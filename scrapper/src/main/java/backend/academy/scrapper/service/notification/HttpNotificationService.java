package backend.academy.scrapper.service.notification;

import backend.academy.scrapper.dto.LinkUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;


@Slf4j
public class HttpNotificationService implements NotificationService {
    private final RestTemplate restTemplate;
    private final String botUrl;

    public HttpNotificationService(RestTemplate restTemplate,
                                    @Value("${bot.base-url}") String botUrl) {
        this.restTemplate = restTemplate;
        this.botUrl = botUrl;
    }

    @Override
    public void sendNotification(LinkUpdateDTO update) {
        restTemplate.postForEntity(botUrl + "/updates", update, Void.class);
    }
}
