package backend.academy.scrapper;

import backend.academy.scrapper.client.BotClient;
import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.StackOverflowClient;
import backend.academy.scrapper.dto.GitHubIssueResponse;
import backend.academy.scrapper.dto.GitHubPullRequestResponse;
import backend.academy.scrapper.dto.LinkUpdateDTO;
import backend.academy.scrapper.entity.LinkUpdate;
import backend.academy.scrapper.repository.LinksRepository;
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

/**
 * LinkCheckScheduler - Класс, который проверяет обновления ссылок
 * на GitHub и Stack Overflow и отправляет уведомления в чаты Telegram.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkCheckScheduler {
    /**
     * GitHubClient - Клиент для работы с GitHub API.
     */
    private final GitHubClient gitHubClient;
    /**
     * StackOverflowClient - Клиент для работы с Stack Overflow API.
     */
    private final StackOverflowClient stackOverflowClient;
    /**
     * BotClient - Клиент для работы с Telegram Bot API.
     */
    private final BotClient botClient;
    /**
     * TgChatRepository - Репозиторий для работы с данными чатов Telegram.
     */
    private final TgChatRepository tgChatRepository;
    /**
     * LinksRepository - Репозиторий для работы с данными ссылок.
     */
    private final LinksRepository linksRepository;
    /**
     * FIXED_RATE - Интервал проверки обновлений.
     */
    private static final int FIXED_RATE = 60000;
    /**
     * MAX_PREVIEW_SIZE - Максимальный размер предварительного просмотра.
     */
    private static final int MAX_PREVIEW_SIZE = 200;
    /**
     * lastUpdates - Хранит последние обновления для каждой ссылки.
     */
    private final Map<String, String> lastUpdates = new ConcurrentHashMap<>();
    /**
     * GITHUB_LINK_REGEX - Регулярное выражение для поиска ссылок на GitHub.
     */
    private static final String GITHUB_LINK_REGEX = "github.com";
    /**
     * STACKOVERFLOW_LINK_REGEX - Регулярное выражение для поиска ссылок.
     * на Stack Overflow
     */
    private static final String STACKOVERFLOW_LINK_REGEX = "stackoverflow.com";
    /**
     * firstCheckTime - Хранит время первой проверки для каждой ссылки.
     */
    private final Map<String, Instant> firstCheckTime
        = new ConcurrentHashMap<>();
    /**
     * processedEntities - Хранит уже обработанные сущности для каждой ссылки.
     */
    private final Map<String, Set<Long>> processedEntities
        = new ConcurrentHashMap<>();

    /**
     * checkForUpdates - Метод для проверки обновлений ссылок.
     * на GitHub и Stack Overflow
     */
    @Scheduled(fixedRate = FIXED_RATE)
    public void checkForUpdates() {
        log.info("🔍 Запуск проверки обновлений...");

        List<Long> chatIds = tgChatRepository.findAllChatIds();

        for (Long chatId : chatIds) {
            List<LinkUpdate> linkUpdates
                = tgChatRepository.findAllLinksByChatId(chatId);

            for (LinkUpdate link : linkUpdates) {
                if (link.url().contains(GITHUB_LINK_REGEX)) {
                    checkGitHubUpdates(link.url(), chatId);
                } else if (link.url().contains(STACKOVERFLOW_LINK_REGEX)) {
                    checkStackOverflowUpdates(link.url(), chatId);
                }
            }
        }
    }

    /**
     * checkGitHubUpdates - Метод для проверки обновлений ссылок.
     * @param link - Ссылка на репозиторий на GitHub
     * @param chatId - Идентификатор чата Telegram
     */
    private void checkGitHubUpdates(final String link, final Long chatId) {
        String[] parts = link.replace("https://github.com/", "").split("/");
        if (parts.length < 2) {
            return;
        }

        String owner = parts[0];
        String repo = parts[1];
        Instant now = Instant.now();

        // Инициализируем время первой проверки для ссылки
        firstCheckTime.putIfAbsent(link, now);

        // Проверка Issues
        gitHubClient.fetchIssues(owner, repo).subscribe(issues -> {
            for (GitHubIssueResponse issue : issues) {
                if (issue.pullRequest() != null) {
                    continue;
                }

                Instant updatedAt = Instant.parse(issue.updatedAt());
                if (shouldProcessUpdate(link, issue.id(), updatedAt)) {
                    String message = formatGitHubMessage(
                        "Issue",
                        issue.title(),
                        issue.user().login(),
                        issue.updatedAt(),
                        issue.body(),
                        issue.state(),
                        issue.htmlUrl()
                    );
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
                    String message = formatGitHubMessage(
                        "Pull Request",
                        pr.title(),
                        pr.user().login(),
                        pr.updatedAt(),
                        pr.body(),
                        pr.state(),
                        pr.htmlUrl()
                    );
                    sendUpdate(link, message, List.of(chatId));
                    markAsProcessed(link, pr.id());
                }
            }
        });
    }

    /**
     * checkGitHubUpdates - Метод для проверки обновлений ссылок.
     * @param link - Ссылка на репозиторий на GitHub
     * @param entityId - Идентификатор сущности
     * @param updatedAt - Время обновления сущности
     * @return - true, если обновление нужно обрабатывать
     */
    private boolean shouldProcessUpdate(
        final String link,
        final Long entityId,
        final Instant updatedAt
    ) {
        // Проверяем появилось ли обновление после начала отслеживания
        boolean isNewAfterStart = updatedAt.isAfter(firstCheckTime.get(link));

        // Проверяем не обрабатывали ли мы уже это обновление
        boolean notProcessedYet = !processedEntities
            .getOrDefault(link, Collections.emptySet())
            .contains(entityId);

        return isNewAfterStart && notProcessedYet;
    }

    /**
     * markAsProcessed - Метод для отметки обработанной сущности.
     * @param link - Ссылка на репозиторий на GitHub
     * @param entityId - Идентификатор сущности
     */
    private void markAsProcessed(final String link, final Long entityId) {
        processedEntities.computeIfAbsent(
            link, _ -> ConcurrentHashMap.newKeySet())
                       .add(entityId);
    }


    private void checkStackOverflowUpdates(
        final String link,
        final Long chatId
    ) {
        log.info("🔍 Проверка обновлений StackOverflow для: {}", link);
        try {
            Long questionId = extractQuestionId(link);
            if (questionId == null) {
                return;
            }

            stackOverflowClient.fetchAnswers(questionId).subscribe(answers -> {
                if (!answers.isEmpty()) {
                    var latestAnswer = answers.getFirst();
                    String message = formatStackOverflowMessage(
                        "Ответ",
                        latestAnswer.questionTitle(),
                        latestAnswer.owner().displayName(),
                        latestAnswer.creationDate(),
                        latestAnswer.body()
                    );
                    if (isUpdated(link, message)) {
                        sendUpdate(link, message, List.of(chatId));
                    }
                }
            });

            stackOverflowClient.fetchComments(questionId).subscribe(
                comments -> {
                    if (!comments.isEmpty()) {
                        var latestComment = comments.getFirst();
                        String message =
                            formatStackOverflowMessage(
                                "Комментарий",
                                latestComment.questionTitle(),
                                latestComment.owner().displayName(),
                                latestComment.creationDate(),
                                latestComment.body()
                            );
                    if (isUpdated(link, message)) {
                        sendUpdate(link, message, List.of(chatId));
                    }
                }
            });
        } catch (Exception e) {
            log.error(
                "Ошибка при проверке обновлений для {}: {}", link,
                e.getMessage(),
                e
            );
        }
    }

    /**
     * sendUpdate - Метод для отправки уведомления в чаты Telegram.
     * @param url - Ссылка на ресурс
     * @param updateContent - Обновленное содержимое сообщения
     * @param chatIds - Идентификаторы чатов Telegram
     */
    private void sendUpdate(final String url,
                            final String updateContent,
                            final List<Long> chatIds) {
        if (!chatIds.isEmpty()) {
            // Получаем текущие теги и фильтры для ссылки
            LinkUpdate linkUpdate = linksRepository.findByUrl(url)
                .orElse(LinkUpdate.builder().url(url).build());

            LinkUpdateDTO dto = LinkUpdateDTO.builder()
                .id(linkUpdate.id())
                .url(url)
                .description(updateContent)
                .tags(linkUpdate.tags())
                .filters(linkUpdate.filters())
                .tgChatIds(chatIds)
                .build();

            botClient.sendUpdate(dto);
        }
    }

    /**
     * isUpdated - Метод для проверки, изменилось ли содержимое сообщения.
     * @param url - Ссылка на ресурс
     * @param newUpdate - Обновленное содержимое сообщения
     * @return true, если содержимое изменилось, иначе false.
     */
    private boolean isUpdated(final String url, final String newUpdate) {
        String lastUpdate = lastUpdates.get(url);
        if (lastUpdate == null || !lastUpdate.equals(newUpdate)) {
            lastUpdates.put(url, newUpdate);
            return true;
        }
        return false;
    }

    /**
     * extractQuestionId - Метод для извлечения идентификатора вопроса
     * из ссылки.
     * @param url - Ссылка на ресурс
     * @return Идентификатор вопроса или null, если не удалось извлечь.
     */
    private Long extractQuestionId(final String url) {
        Pattern pattern = Pattern.compile(
            "stackoverflow.com/questions/(\\d+)"
        );
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }

    /**
     * formatGitHubMessage - Метод для форматирования сообщения
     * о GitHub обновлении.
     * @param type - Тип обновления (Issue или Pull Request)
     * @param title - Заголовок
     * @param author - Автор
     * @param date - Дата
     * @param body - Тело
     * @param state - Статус
     * @param url - Ссылка
     * @return - Форматированное сообщение.
     */
    String formatGitHubMessage(
        final String type,
        final String title,
        final String author,
        final String date,
        final String body,
        final String state,
        final String url
    ) {
        return String.format(
            "[%s] %s\nАвтор: %s\nДата: %s\n%s%sОписание: %s",
            type,
            title,
            author,
            date,
            (state != null ? "Статус: " + state + "\n" : ""),
            (url != null ? "Ссылка: " + url + "\n" : ""),
            body != null ? body.substring(
                0,
                Math.min(body.length(), MAX_PREVIEW_SIZE)
            ) : "Без описания"
        );
    }

    /**
     * formatStackOverflowMessage - Метод для форматирования сообщения.
     * @param type - Тип обновления (Ответ или Комментарий)
     * @param questionTitle - Заголовок вопроса
     * @param user - Автор
     * @param date - Дата
     * @param body - Тело
     * @return - Форматированное сообщение.
     */
    String formatStackOverflowMessage(
        final String type,
        final String questionTitle,
        final String user,
        final String date,
        final String body
    ) {
        return String.format(
            "[%s] Вопрос: %s\nАвтор: %s\nДата: %s\nПревью: %s",
            type,
            questionTitle,
            user,
            date,
            body != null ? body.substring(
                0,
                Math.min(body.length(), MAX_PREVIEW_SIZE)
            ) : "Без текста"
        );
    }
}
