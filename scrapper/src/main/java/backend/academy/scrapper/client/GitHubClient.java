package backend.academy.scrapper.client;



import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.dto.GitHubRepositoryResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class GitHubClient {

    private final WebClient webClient;

    public GitHubClient(ScrapperConfig scrapperConfig) {
        this.webClient = WebClient.builder()
                .baseUrl(scrapperConfig.githubApiUrl())
                .defaultHeader("Authorization", "Bearer " + scrapperConfig.github().token())
                .defaultHeader("Accept", "application/vnd.github.v3+json")
                .build();
    }

    public Mono<GitHubRepositoryResponse> fetchRepositoryInfo(String owner, String repo) {
        return webClient.get()
                .uri("/repos/{owner}/{repo}", owner, repo)
                .retrieve()
                .bodyToMono(GitHubRepositoryResponse.class);
    }
}
