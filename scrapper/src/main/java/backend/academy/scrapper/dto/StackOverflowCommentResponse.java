package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * StackOverflowCommentResponse - класс для работы с данными комментариев к StackOverflowQuestionResponse.
 * @param body
 * @param owner
 * @param creationDate
 */
public record StackOverflowCommentResponse(
    @JsonProperty("body") String body,
    @JsonProperty("owner") StackOverflowUser owner,
    @JsonProperty("creation_date") String creationDate,
    @JsonProperty("questionTitle") String questionTitle
) {
}
