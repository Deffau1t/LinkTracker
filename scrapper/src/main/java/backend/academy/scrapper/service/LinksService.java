package backend.academy.scrapper.service;

import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.StackOverflowClient;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.repository.LinkTrackingRepository;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Сервис для отслеживания ссылок и проверки обновлений.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class LinksService {

    /**
     * Клиент для GitHub API.
     */
    private final GitHubClient gitHubClient;

    /**
     * Клиент для Stack Overflow API.
     */
    private final StackOverflowClient stackOverflowClient;

    /**
     * Репозиторий для отслеживания ссылок.
     */
    private final LinkTrackingRepository linkTrackingRepository;

    /**
     * Метод для добавления ссылки для отслеживания.
     * @param chatId - идентификатор чата
     * @param linkResponse - ответ с информацией о ссылке
     */
    public void addLinkOfChat(final Long chatId,
                              final LinkResponse linkResponse) {
        linkTrackingRepository.trackLink(chatId, linkResponse.url());
        checkLinkForUpdates(linkResponse.url());
    }

    /**
     * Метод для удаления ссылки из отслеживания.
     * @param chatId - идентификатор чата
     * @param removeLinkRequest - запрос на удаление ссылки
     */
    public void deleteLinkOfChat(final Long chatId,
                                 final RemoveLinkRequest removeLinkRequest) {
        linkTrackingRepository.untrackLink(chatId, removeLinkRequest.link());
    }

    /**
     * Метод для проверки обновлений ссылки.
     * @param url - ссылка для проверки обновлений
     */
    private void checkLinkForUpdates(final String url) {
        if (isGitHubLink(url)) {
            fetchGitHubUpdates(url);
        } else if (isStackOverflowLink(url)) {
            fetchStackOverflowUpdates(url);
        }
    }

    /**
     * Метод для проверки, является ли ссылка GitHub ссылкой.
     * @param url - ссылка для проверки
     * @return - true, если ссылка является GitHub ссылкой, иначе false
     */
    private boolean isGitHubLink(final String url) {
        return url.contains("github.com");
    }

    /**
     * Метод для проверки, является ли ссылка Stack Overflow ссылкой.
     * @param url - ссылка для проверки
     * @return - true, если ссылка является Stack Overflow ссылкой, иначе false
     */
    private boolean isStackOverflowLink(final String url) {
        return url.contains("stackoverflow.com");
    }

    /**
     * Метод для проверки обновлений GitHub ссылки.
     * @param url - ссылка для проверки обновлений
     */
    public void fetchGitHubUpdates(final String url) {
        Pattern pattern = Pattern.compile(
            "https://github.com/([^/]+)/([^/]+)"
        );
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            String owner = matcher.group(1);
            String repo = matcher.group(2);
            gitHubClient.fetchRepositoryInfo(owner, repo)
                    .subscribe(response -> {
                        log.info(
                            "GitHub repository {} last updated at: {}",
                            response.fullName(),
                            response.updatedAt()
                        );
                    }, error -> log.error(
                        "Error fetching GitHub repository info: {}",
                        error.getMessage())
                    );
        }
    }

    /**
     * Метод для проверки обновлений Stack Overflow ссылки.
     * @param url - ссылка для проверки обновлений
     */
    public void fetchStackOverflowUpdates(final String url) {
        Pattern pattern = Pattern.compile(
            "https://stackoverflow.com/questions/(\\d+)"
        );
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            Long questionId = Long.parseLong(matcher.group(1));
            stackOverflowClient.fetchQuestionInfo(questionId)
                    .subscribe(response -> {
                        log.info(
                            "StackOverflow question {} last activity at: {}",
                            response.questionId(), response.lastActivityDate()
                        );
                    }, error -> log.error(
                        "Error fetching StackOverflow question info: {}",
                        error.getMessage())
                    );
        }
    }
}
