package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * StackOverflowCommentResponse -
 * класс для работы с данными комментариев к StackOverflowQuestionResponse.
 * @param body - текст комментария
 * @param owner - владелец комментария
 * @param creationDate - дата создания комментария
 * @param questionTitle - заголовок вопроса
 * на который был дан ответ на сайте stackoverflow.com
 */
public record StackOverflowCommentResponse(
    @JsonProperty("body") String body,
    @JsonProperty("owner") StackOverflowUser owner,
    @JsonProperty("creation_date") String creationDate,
    @JsonProperty("questionTitle") String questionTitle
) {
}
