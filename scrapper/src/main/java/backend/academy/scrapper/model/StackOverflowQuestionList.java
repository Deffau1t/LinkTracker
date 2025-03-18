package backend.academy.scrapper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

/**
 * StackOverflowQuestionList - класс для получения списка вопросов
 * с сайта Stack Overflow.
 */

@Getter
public class StackOverflowQuestionList {
    /**
     * items - список вопросов.
     */
    @JsonProperty("items")
    private List<Object> items;
}
