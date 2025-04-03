package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * StackOverflowAnswerResponse - класс для работы с данными ответа.
 * @param body текст ответа на вопрос
 * @param owner владелец ответа на вопрос
 * @param creationDate дата создания ответа на вопрос
 * @param questionTitle заголовок вопроса
 * на который был дан ответ на сайте stackoverflow.com
 */
public record StackOverflowAnswerResponse(
    @JsonProperty("body") String body,
    @JsonProperty("owner") StackOverflowUser owner,
    @JsonProperty("creation_date") String creationDate,
    @JsonProperty("questionTitle") String questionTitle
) {
}
