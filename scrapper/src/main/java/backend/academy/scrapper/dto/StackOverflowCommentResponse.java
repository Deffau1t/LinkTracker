package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * StackOverflowCommentResponse - класс для работы с данными комментариев к
 * StackOverflowQuestionResponse.
 * @param body
 */

public record StackOverflowCommentResponse(
    @JsonProperty("body") String body
) {

}

