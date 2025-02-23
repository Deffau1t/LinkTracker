package backend.academy.scrapper;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ScrapperConfig(
    @NotEmpty String githubApiUrl,
    @NotEmpty String stackoverflowApiUrl,
    GitHubCredentials github,
    StackOverflowCredentials stackOverflow
) {
    public record GitHubCredentials(
        @NotEmpty String token
    ) {}

    public record StackOverflowCredentials(
        @NotEmpty String key,
        @NotEmpty String accessToken
    ) {}
}
