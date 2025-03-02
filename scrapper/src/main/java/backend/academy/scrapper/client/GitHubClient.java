package backend.academy.scrapper.client;



import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.dto.GitHubRepositoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GitHubClient {

    private final WebClient webClient;

    public GitHubClient(ScrapperConfig scrapperConfig) {
    this.webClient = WebClient.builder()
            .baseUrl(scrapperConfig.githubApiUrl())
            .defaultHeader("Authorization", "token " + scrapperConfig.github().token()) // Исправлено
            .defaultHeader("Accept", "application/vnd.github.v3+json")
            .build();
}


    public Mono<GitHubRepositoryResponse> fetchRepositoryInfo(String owner, String repo) {
        return webClient.get()
                .uri("/repos/{owner}/{repo}", owner, repo)
                .retrieve()
                .bodyToMono(GitHubRepositoryResponse.class)
                .doOnError(e -> log.error("Ошибка при запросе к GitHub API: {}", e.getMessage()));
    }
}
