package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotClient {

    @Autowired
    private final RestTemplate restTemplate;

    @Value("${bot.base-url}")
    private String botBaseUrl;

    public void sendUpdate(LinkUpdate update) {
        String url = botBaseUrl + "/updates";

        try {
            restTemplate.postForEntity(url, update, String.class);
            log.info("Обновление отправлено в bot: {}", update.url());
        } catch (Exception e) {
            log.error("Ошибка при отправке обновления в bot: {}", e.getMessage());
        }
    }
}
