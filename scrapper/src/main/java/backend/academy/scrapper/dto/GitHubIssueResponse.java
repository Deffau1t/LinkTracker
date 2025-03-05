package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GitHubIssueResponse - класс для работы с данными проекта.
 * @param title
 */

public record GitHubIssueResponse(
    @JsonProperty("title") String title
) {

}

