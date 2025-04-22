package backend.academy.scrapper.client;


import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.dto.GitHubCommitResponse;
import backend.academy.scrapper.dto.GitHubIssueResponse;
import backend.academy.scrapper.dto.GitHubPullRequestResponse;
import backend.academy.scrapper.dto.GitHubRepositoryResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Клиент для работы с GitHub API.
 */

@Slf4j
@Component
public class GitHubClient {

    /**
     * webClient - объект для работы с GitHub API.
     */
    private final WebClient webClient;

    /**
     * Конструктор класса GitHubClient.
     *
     * @param scrapperConfig - объект конфигурации для доступа к GitHub API.
     */
    public GitHubClient(final ScrapperConfig scrapperConfig) {
        this.webClient = WebClient.builder()
                .baseUrl(scrapperConfig.githubApiUrl())
                .defaultHeader("Authorization",
                    "token " + scrapperConfig.github().token())
                .defaultHeader("Accept", "application/vnd.github.v3+json")
                .build();
    }

    /**
     * Метод для получения информации о репозитории на GitHub.
     * @param owner - владелец репозитория.
     * @param repo - название репозитория.
     * @return - Mono с объектом GitHubRepositoryResponse.
     */
    public Mono<GitHubRepositoryResponse> fetchRepositoryInfo(
        final String owner,
        final String repo) {
        return webClient.get()
                .uri("/repos/{owner}/{repo}", owner, repo)
                .retrieve()
                .bodyToMono(GitHubRepositoryResponse.class)
                .doOnError(
                    e -> log.error("Ошибка при запросе к GitHub API: {}",
                    e.getMessage())
                );
    }

    /**
     * Метод для получения списка коммитов в репозитории на GitHub.
     * @param owner - владелец репозитория.
     * @param repo - название репозитория.
     * @return - Mono с объектом GitHubCommitResponse.
     */
    public Mono<List<GitHubCommitResponse>> fetchCommits(
        final String owner,
        final String repo) {
        return webClient.get()
                .uri("/repos/{owner}/{repo}/commits", owner, repo)
                .retrieve()
                .bodyToFlux(GitHubCommitResponse.class)
                .collectList();
    }

    /**
     * Метод для получения списка коммитов в репозитории на GitHub.
     * @param owner - владелец репозитория.
     * @param repo - название репозитория.
     * @return - Mono с объектом GitHubCommitResponse.
     */
    public Mono<List<GitHubIssueResponse>> fetchIssues(
        final String owner,
        final String repo) {
        return webClient.get()
                .uri("/repos/{owner}/{repo}/issues", owner, repo)
                .retrieve()
                .bodyToFlux(GitHubIssueResponse.class)
                .collectList();
    }

    /**
     * Метод для получения списка коммитов в репозитории на GitHub.
     * @param owner - владелец репозитория.
     * @param repo - название репозитория.
     * @return - Mono с объектом GitHubCommitResponse.
     */
    public Mono<List<GitHubPullRequestResponse>> fetchPullRequests(
        final String owner,
        final String repo
    ) {
        return webClient.get()
            .uri("/repos/{owner}/{repo}/pulls?state=all", owner, repo)
            .retrieve()
            .bodyToFlux(GitHubPullRequestResponse.class)
            .collectList()
            .doOnNext(prs -> log.info(
                "Found {} PRs for {}/{}",
                prs.size(),
                owner,
                repo)
            )
            .doOnError(e -> log.error(
                "GitHub PRs fetch error: {}",
                e.getMessage())
            );
    }
}
