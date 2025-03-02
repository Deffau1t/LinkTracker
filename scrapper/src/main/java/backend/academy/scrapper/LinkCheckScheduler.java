package backend.academy.scrapper;

import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.StackOverflowClient;
import backend.academy.scrapper.dto.GitHubRepositoryResponse;
import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.dto.StackOverflowQuestionResponse;
import backend.academy.scrapper.repository.LinkTrackingRepository;
import backend.academy.scrapper.service.BotClient;
import backend.academy.scrapper.service.LinksService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkCheckScheduler {

    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;
    private final BotClient botClient;
    private final LinkTrackingRepository linkTrackingRepository;

    @Scheduled(fixedRate = 60000)
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

    private void checkGitHubUpdates(String link) {
        String[] parts = link.replace("https://github.com/", "").split("/");
        if (parts.length < 2) return;

        String owner = parts[0];
        String repo = parts[1];

        GitHubRepositoryResponse repoResponse = gitHubClient.fetchRepositoryInfo(owner, repo).block();
        if (repoResponse != null) {
            log.info("Последнее обновление {}: {}", link, repoResponse.updatedAt());
            sendUpdate(link, "Обнаружено новое обновление в репозитории!");
        }
    }

    private void checkStackOverflowUpdates(String link) {
        try {
            Long questionId = Long.parseLong(link.replaceAll("\\D+", ""));
            StackOverflowQuestionResponse questionResponse = stackOverflowClient.fetchQuestionInfo(questionId).block();
            if (questionResponse != null) {
                log.info("Последнее обновление {}: {}", link, questionResponse.lastActivityDate());
                sendUpdate(link, "Обнаружено новое обновление в вопросе!");
            }
        } catch (Exception e) {
            log.error("Ошибка при проверке обновлений для {}", link, e);
        }
    }

    private void sendUpdate(String url, String description) {
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
