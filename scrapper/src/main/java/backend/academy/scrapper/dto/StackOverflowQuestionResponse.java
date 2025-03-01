package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class StackOverflowQuestionResponse {
    @JsonProperty("question_id")
    private Long questionId;
    private String title;
    @JsonProperty("link")
    private String link;
    @JsonProperty("last_activity_date")
    private OffsetDateTime lastActivityDate;
}
