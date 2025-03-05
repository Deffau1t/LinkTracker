package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * StackOverflowAnswerResponse - класс для работы с данными ответа.
 * @param body
 */

public record StackOverflowAnswerResponse(
    @JsonProperty("body") String body
) {

}

