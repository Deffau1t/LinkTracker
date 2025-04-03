package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GitHubIssueResponse - класс для представления данных об issue на GitHub.
 * @param id - идентификатор
 * @param title - название
 * @param user - пользователь
 * @param updatedAt - дата обновления
 * @param body - описание
 * @param state - состояние
 * @param htmlUrl - ссылка на issue
 * @param pullRequest - ссылка на пулл реквест
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubIssueResponse(
    @JsonProperty("id") Long id,
    @JsonProperty("title") String title,
    @JsonProperty("user") GitHubUser user,
    @JsonProperty("updated_at")
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
        timezone = "UTC"
    )
    String updatedAt,
    @JsonProperty("body") String body,
    @JsonProperty("state") String state,
    @JsonProperty("html_url") String htmlUrl,
    @JsonProperty("pull_request")
    Object pullRequest
) {
}
