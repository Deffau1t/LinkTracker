package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GitHubCommentResponse - класс для работы с данными комментариев.
 * @param body
 */

public record GitHubCommentResponse(
    @JsonProperty("body") String body
) {

}

