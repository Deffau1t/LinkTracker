package backend.academy.bot;

import backend.academy.bot.model.LinkUpdate;
import backend.academy.bot.service.LinkTrackerBot;
import backend.academy.bot.service.LinkUpdateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class LinkCheckScheduler {

    private final LinkUpdateService linkUpdateService;
    private final LinkTrackerBot linkTrackerBot;

    @Scheduled(fixedRate = 30000)
    public void checkForUpdates() {
         LinkUpdate dummyUpdate = LinkUpdate.builder()
                .url("https://example.com")
                .description("Обнаружено новое обновление репозитория.")
                .tgChatIds(linkUpdateService.chatSubscribes().keySet().stream().toList())
                .build();

         log.info("Запуск проверки обновлений...");
         linkUpdateService.updateLink(dummyUpdate, linkTrackerBot);
    }
}
