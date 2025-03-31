package backend.academy.scrapper;

import backend.academy.scrapper.client.BotClient;
import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.StackOverflowClient;
import backend.academy.scrapper.dto.GitHubIssueResponse;
import backend.academy.scrapper.dto.GitHubPullRequestResponse;
import backend.academy.scrapper.entity.LinkUpdate;
import backend.academy.scrapper.repository.TgChatRepository;
import java.time.Instant;
import java.util.Collections;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkCheckScheduler {

    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;
    private final BotClient botClient;
    private final TgChatRepository tgChatRepository;

    private static final int FIXED_RATE = 60000;
    private final Map<String, String> lastUpdates = new ConcurrentHashMap<>();

    private static final String GITHUB_LINK_REGEX = "github.com";
    private static final String STACKOVERFLOW_LINK_REGEX = "stackoverflow.com";

    private final Map<String, String> lastSeenUpdates = new ConcurrentHashMap<>();
    private final Set<String> initializedLinks = ConcurrentHashMap.newKeySet();

    private final Map<String, Instant> firstCheckTime = new ConcurrentHashMap<>();
    private final Map<String, Set<Long>> processedEntities = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = FIXED_RATE)
    public void checkForUpdates() {
        log.info("🔍 Запуск проверки обновлений...");

        List<Long> chatIds = tgChatRepository.getAllChatIds();

        for (Long chatId : chatIds) {
            List<LinkUpdate> linkUpdates = tgChatRepository.getAllLinks(chatId);

            for (LinkUpdate link : linkUpdates) {
                if (link.url().contains(GITHUB_LINK_REGEX)) {
                    checkGitHubUpdates(link.url(), chatId);
                } else if (link.url().contains(STACKOVERFLOW_LINK_REGEX)) {
                    checkStackOverflowUpdates(link.url(), chatId);
                }
            }
        }
    }

    private void checkGitHubUpdates(final String link, Long chatId) {
        String[] parts = link.replace("https://github.com/", "").split("/");
        if (parts.length < 2) return;

        String owner = parts[0];
        String repo = parts[1];
        Instant now = Instant.now();

        // Инициализируем время первой проверки для ссылки
        firstCheckTime.putIfAbsent(link, now);

        // Проверка Issues
        gitHubClient.fetchIssues(owner, repo).subscribe(issues -> {
            for (GitHubIssueResponse issue : issues) {
                if (issue.pullRequest() != null) continue;

                Instant updatedAt = Instant.parse(issue.updatedAt());
                if (shouldProcessUpdate(link, issue.id(), updatedAt)) {
                    String message = formatGitHubMessage("Issue", issue.title(),
                            issue.user().login(), issue.updatedAt(), issue.body(),
                            issue.state(), issue.htmlUrl());
                    sendUpdate(link, message, List.of(chatId));
                    markAsProcessed(link, issue.id());
                }
            }
        });

        // Проверка Pull Requests
        gitHubClient.fetchPullRequests(owner, repo).subscribe(pullRequests -> {
            for (GitHubPullRequestResponse pr : pullRequests) {
                Instant updatedAt = Instant.parse(pr.updatedAt());
                if (shouldProcessUpdate(link, pr.id(), updatedAt)) {
                    String message = formatGitHubMessage("Pull Request", pr.title(),
                            pr.user().login(), pr.updatedAt(), pr.body(),
                            pr.state(), pr.htmlUrl());
                    sendUpdate(link, message, List.of(chatId));
                    markAsProcessed(link, pr.id());
                }
            }
        });
    }

    private boolean shouldProcessUpdate(String link, Long entityId, Instant updatedAt) {
        // Проверяем появилось ли обновление после начала отслеживания
        boolean isNewAfterStart = updatedAt.isAfter(firstCheckTime.get(link));

        // Проверяем не обрабатывали ли мы уже это обновление
        boolean notProcessedYet = !processedEntities
            .getOrDefault(link, Collections.emptySet())
            .contains(entityId);

        return isNewAfterStart && notProcessedYet;
    }

    private void markAsProcessed(String link, Long entityId) {
        processedEntities.computeIfAbsent(link, k -> ConcurrentHashMap.newKeySet())
                       .add(entityId);
    }


    private void checkStackOverflowUpdates(final String link, Long chatId) {
        log.info("🔍 Проверка обновлений StackOverflow для: {}", link);
        try {
            Long questionId = extractQuestionId(link);
            if (questionId == null) {
                return;
            }

            stackOverflowClient.fetchAnswers(questionId).subscribe(answers -> {
                if (!answers.isEmpty()) {
                    var latestAnswer = answers.getFirst();
                    String message = formatStackOverflowMessage("Ответ", latestAnswer.questionTitle(), latestAnswer.owner().displayName(), latestAnswer.creationDate(), latestAnswer.body());
                    if (isUpdated(link, message)) {
                        sendUpdate(link, message, List.of(chatId));
                    }
                }
            });

            stackOverflowClient.fetchComments(questionId).subscribe(comments -> {
                if (!comments.isEmpty()) {
                    var latestComment = comments.getFirst();
                    String message = formatStackOverflowMessage("Комментарий", latestComment.questionTitle(), latestComment.owner().displayName(), latestComment.creationDate(), latestComment.body());
                    if (isUpdated(link, message)) {
                        sendUpdate(link, message, List.of(chatId));
                    }
                }
            });
        } catch (Exception e) {
            log.error("🚨 Ошибка при проверке обновлений для {}: {}", link, e.getMessage(), e);
        }
    }

    private void sendUpdate(final String url, final String description, List<Long> chatIds) {
        if (!chatIds.isEmpty()) {
            botClient.sendUpdate(LinkUpdate.builder()
                    .url(url)
                    .description(description)
                    .tgChatIds(chatIds)
                    .build());
        }
    }

    private boolean isUpdated(final String url, final String newUpdate) {
        String lastUpdate = lastUpdates.get(url);
        if (lastUpdate == null || !lastUpdate.equals(newUpdate)) {
            lastUpdates.put(url, newUpdate);
            return true;
        }
        return false;
    }

    private Long extractQuestionId(final String url) {
        Pattern pattern = Pattern.compile("stackoverflow.com/questions/(\\d+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }

    private String formatGitHubMessage(String type, String title, String author, String date, String body, String state, String url) {
        return String.format("[%s] %s\nАвтор: %s\nДата: %s\n%s%sОписание: %s", type, title, author, date,
                (state != null ? "Статус: " + state + "\n" : ""),
                (url != null ? "Ссылка: " + url + "\n" : ""),
                body != null ? body.substring(0, Math.min(body.length(), 200)) : "Без описания");
    }

    private String formatStackOverflowMessage(String type, String questionTitle, String user, String date, String body) {
        return String.format("[%s] Вопрос: %s\nАвтор: %s\nДата: %s\nПревью: %s", type, questionTitle, user, date,
                body != null ? body.substring(0, Math.min(body.length(), 200)) : "Без текста");
    }
}
