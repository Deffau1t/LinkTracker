package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class StackOverflowQuestionResponse {
    /**
     * Id вопроса на stackoverflow.com.
     */
    @JsonProperty("question_id")
    private Long questionId;

    /**
     * Заголовок вопроса.
     */
    private String title;

    /**
     * Ссылка на вопрос.
     */
    @JsonProperty("link")
    private String link;

    /**
     * Дата последнего обновления вопроса.
     */
    @JsonProperty("last_activity_date")
    private OffsetDateTime lastActivityDate;
}
