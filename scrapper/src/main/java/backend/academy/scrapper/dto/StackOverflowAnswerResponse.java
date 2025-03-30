package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * StackOverflowAnswerResponse - класс для работы с данными ответа.
 * @param body
 * @param owner
 * @param creationDate
 */
public record StackOverflowAnswerResponse(
    @JsonProperty("body") String body,
    @JsonProperty("owner") StackOverflowUser owner,
    @JsonProperty("creation_date") String creationDate,
    @JsonProperty("questionTitle") String questionTitle
) {
}
