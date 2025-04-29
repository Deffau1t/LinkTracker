package backend.academy.scrapper.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * ScrapperConfig - конфигурация для скраппера.
 * @param githubApiUrl - ссылка на api github
 * @param stackoverflowApiUrl - ссылка на api stackoverflow
 * @param github - данные для доступа к github api
 * @param stackOverflow - данные для доступа к stackoverflow api
 */

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ScrapperConfig(
    @NotEmpty String githubApiUrl,
    @NotEmpty String stackoverflowApiUrl,
    GitHubCredentials github,
    StackOverflowCredentials stackOverflow,
    KafkaConfig kafka,
    @NotEmpty String messageTransport
) {
    /**
    * GitHubCredentials - данные для доступа к github api.
    * @param token - токен для доступа к github api
     */
    public record GitHubCredentials(
        @NotEmpty String token
    ) {

    }

    /**
     * StackOverflowCredentials - данные для доступа к stackoverflow api.
     * @param key
     * @param accessToken
     */
    public record StackOverflowCredentials(
        @NotEmpty String key,
        @NotEmpty String accessToken
    ) {

    }

    public record KafkaConfig(
        @NotEmpty String topicNotifications
    ) {
    }
}
