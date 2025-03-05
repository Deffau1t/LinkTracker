package backend.academy.scrapper;

import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.StackOverflowClient;
import backend.academy.scrapper.dto.GitHubRepositoryResponse;
import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.dto.StackOverflowQuestionResponse;
import backend.academy.scrapper.repository.LinkTrackingRepository;
import backend.academy.scrapper.service.BotClient;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
     * Хранит последнее известное обновление для каждой ссылки
     */
    private final Map<String, String> lastUpdates = new ConcurrentHashMap<>();

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
     * Проверяем обновления в GitHub (коммиты, issues, комментарии)
     * @param link - Ссылка на репозиторий на GitHub.
     */
    private void checkGitHubUpdates(final String link) {
        String[] parts = link.replace(
            "https://github.com/",
            "")
            .split("/");

        if (parts.length < 2) {
            return;
        }

        String owner = parts[0];
        String repo = parts[1];

        gitHubClient.fetchCommits(owner, repo).subscribe(commits -> {
            if (!commits.isEmpty()) {
                String latestCommit = commits.get(0).commit().message();
                if (isUpdated(link, latestCommit)) {
                    sendUpdate(
                        link,
                        "Новый коммит в " + repo + ": " + latestCommit
                    );
                }
            }
        });


        gitHubClient.fetchIssues(owner, repo).subscribe(issues -> {
            if (!issues.isEmpty()) {
                 String latestIssue = issues.get(0).title();
                 if (isUpdated(link, latestIssue)) {
                     sendUpdate(link,
                         "Новое issue в " + repo + ": " + latestIssue);
                 }
            }
        });

        gitHubClient.fetchComments(owner, repo).subscribe(comments -> {
            if (!comments.isEmpty()) {
                String latestComment = comments.get(0).body();
                if (isUpdated(link, latestComment)) {
                    sendUpdate(link,
                        "Новый комментарий в " + repo + ": " + latestComment);
                }
            }
        });
    }

    /**
     * Проверяем обновления в StackOverflow (ответы, комментарии)
     * @param link - Ссылка на вопрос на StackOverflow.
     */
    private void checkStackOverflowUpdates(final String link) {
        log.info("🔍 Проверка обновлений StackOverflow для: {}", link);
        try {
            Long questionId = extractQuestionId(link);
            if (questionId == null) {
                log.error(
                    "❌ Не удалось извлечь ID вопроса из ссылки: {}",
                    link
                );
                return;
            }

            log.info(
                "📡 Отправка запроса на ответы StackOverflow для {}",
                questionId);
            stackOverflowClient.fetchAnswers(questionId).subscribe(answers -> {
                if (answers == null || answers.isEmpty()) {
                    log.info(
                        "⚠️ Нет новых ответов для вопроса {}",
                        questionId);
                    return;
                }

                String latestAnswer = answers.get(0).body();
                if (latestAnswer != null && isUpdated(link, latestAnswer)) {
                    sendUpdate(
                        link,
                        "📌 Новый ответ в StackOverflow: " + latestAnswer
                    );
                }
            }, error -> log.error(
                "❌ Ошибка при запросе ответов StackOverflow: {}",
                error.getMessage())
            );

            log.info(
                "📡 Отправка запроса на комментарии StackOverflow для {}",
                questionId
            );

            stackOverflowClient.fetchComments(questionId).subscribe(
                comments -> {
                    if (comments == null || comments.isEmpty()) {
                        log.info(
                            "⚠️ Нет новых комментариев для вопроса {}",
                            questionId);
                        return;
                    }

                    String latestComment = comments.get(0).body();
                    if (latestComment != null
                        && isUpdated(link, latestComment)) {
                            sendUpdate(
                                link,
                                "💬 Новый комментарий в StackOverflow: "
                                    + latestComment
                            );
                    }
            }, error -> log.error(
                "❌ Ошибка при запросе комментариев StackOverflow: {}",
                    error.getMessage())
            );

        } catch (Exception e) {
            log.error(
                "🚨 Ошибка при проверке обновлений для {}: {}",
                link,
                e.getMessage(), e);
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

    /**
     * Проверяет, есть ли новое обновление по ссылке
     * @param url - Ссылка на ресурс.
     *            newUpdate - Новое обновление.
     */
    private boolean isUpdated(final String url, final String newUpdate) {
        String lastUpdate = lastUpdates.get(url);
        if (lastUpdate == null || !lastUpdate.equals(newUpdate)) {
            lastUpdates.put(url, newUpdate);
            return true;
        }
        return false;
    }

    private Long extractQuestionId(String url) {
        Pattern pattern = Pattern.compile(
            "stackoverflow.com/questions/(\\d+)"
        );
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            Long questionId = Long.parseLong(matcher.group(1));
            log.info("✅ Извлечен ID вопроса: {}", questionId);
            return questionId;
        }
        log.error("❌ Не удалось извлечь ID вопроса из: {}", url);
        return null;
    }
}
