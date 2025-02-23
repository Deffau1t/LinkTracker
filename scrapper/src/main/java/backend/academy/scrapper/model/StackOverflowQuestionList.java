package backend.academy.scrapper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class StackOverflowQuestionList {
    @JsonProperty("items")
    private List<Object> items;
}
