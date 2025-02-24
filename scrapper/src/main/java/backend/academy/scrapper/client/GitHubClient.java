package backend.academy.scrapper.client;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.model.GitHubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GitHubClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public GitHubClient(ScrapperConfig config) {
        this.baseUrl = config.githubApiUrl();
        this.restTemplate = new RestTemplate();
    }

    public GitHubRepository getRepositoryInfo(String owner, String repo) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment("repos", owner, repo)
                .build().toUriString();
        return restTemplate.getForObject(url, GitHubRepository.class);
    }
}
