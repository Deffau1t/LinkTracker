package backend.academy.scrapper;

import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.StackOverflowClient;
import backend.academy.scrapper.dto.GitHubRepositoryResponse;
import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.dto.StackOverflowQuestionResponse;
import backend.academy.scrapper.repository.LinkTrackingRepository;
import backend.academy.scrapper.service.BotClient;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Класс для проверки обновлений ссылок.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkCheckScheduler {

    /**
     * gitHubClient - Клиент для GitHub API.
     */
    private final GitHubClient gitHubClient;

    /**
     * stackOverflowClient - Клиент для Stack Overflow API.
     */
    private final StackOverflowClient stackOverflowClient;

    /**
     * botClient - Клиент для бота.
     */
    private final BotClient botClient;

    /**
     * linkTrackingRepository - Репозиторий для отслеживания ссылок.
     */
    private final LinkTrackingRepository linkTrackingRepository;

    /**
     * FIXED_RATE - Периодичность проверки обновлений в миллисекундах.
     */
    private static final int FIXED_RATE = 60000;

    /**
     * Метод для проверки обновлений ссылок.
     */
    @Scheduled(fixedRate = FIXED_RATE)
    public void checkForUpdates() {
        log.info("Запуск проверки обновлений...");

        Set<String> trackedLinks = linkTrackingRepository.getAllTrackedLinks();

        for (String link : trackedLinks) {
            if (link.contains("github.com")) {
                checkGitHubUpdates(link);
            } else if (link.contains("stackoverflow.com")) {
                checkStackOverflowUpdates(link);
            }
        }
    }

    /**
     * Метод для проверки обновлений GitHub репозитория.
     * @param link - Ссылка на GitHub репозиторий.
     */
    private void checkGitHubUpdates(final String link) {
        String[] parts = link.replace(
            "https://github.com/", "")
            .split("/");
        if (parts.length < 2) {
            return;
        }

        String owner = parts[0];
        String repo = parts[1];

        GitHubRepositoryResponse repoResponse = gitHubClient
            .fetchRepositoryInfo(owner, repo)
            .block();
        if (repoResponse != null) {
            log.info(
                "Последнее обновление {}: {}",
                link,
                repoResponse.updatedAt()
            );
            sendUpdate(link, "Обнаружено новое обновление в репозитории!");
        }
    }

    /**
     * Метод для проверки обновлений Stack Overflow вопроса.
     * @param link - Ссылка на Stack Overflow вопрос.
     */
    private void checkStackOverflowUpdates(final String link) {
        try {
            Long questionId = Long.parseLong(link.replaceAll("\\D+", ""));
            StackOverflowQuestionResponse questionResponse = stackOverflowClient
                .fetchQuestionInfo(questionId)
                .block();
            if (questionResponse != null) {
                log.info(
                    "Последнее обновление {}: {}",
                    link,
                    questionResponse.lastActivityDate()
                );
                sendUpdate(link, "Обнаружено новое обновление в вопросе!");
            }
        } catch (Exception e) {
            log.error("Ошибка при проверке обновлений для {}", link, e);
        }
    }

    /**
     * Метод для отправки уведомления о новом обновлении.
     * @param url - Ссылка на обновленный ресурс.
     * @param description - Описание обновления.
     */
    private void sendUpdate(final String url, final String description) {
        List<Long> chatIds = linkTrackingRepository.getChatIdsForLink(url);
        if (!chatIds.isEmpty()) {
            botClient.sendUpdate(LinkUpdate.builder()
                    .url(url)
                    .description(description)
                    .tgChatIds(chatIds)
                    .build());
        }
    }

}
