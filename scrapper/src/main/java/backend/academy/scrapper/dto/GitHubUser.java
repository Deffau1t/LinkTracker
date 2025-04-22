package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GitHubUser - класс для представления пользователя GitHub.
 * @param login
 */
public record GitHubUser(
    @JsonProperty("login") String login
) {
}
