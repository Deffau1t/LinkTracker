package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.util.List;

/**
 * StackOverflowResponseWrapper - обертка для ответа от Stack Overflow API.
 * @param <T> - тип данных, которые возвращаются от API.
 */

@Getter
public class StackOverflowResponseWrapper<T> {
    @JsonProperty("items")
    private List<T> items;
}
