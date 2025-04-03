package backend.academy.bot.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * LinkResponse - дто для ответа по запросу на получение ссылки по ID.
 */

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkResponse {
    /**
     * Id - идентификатор ссылки.
     */
    private Long id;

    /**
     * url - ссылка.
     */
    private String url;

    /**
     * tags - теги.
     */
    private List<String> tags;

    /**
     * filters - фильтры.
     */
    private List<String> filters;

    /**
     * toString - метод для получения строки с данными ссылки.
     * @return - строка с данными ссылки.
     */
    @Override
    public String toString() {
        return String.format(
            "URL: %s | Tags: %s | Filters: %s \n",
            url,
            tags,
            filters
        );
    }
}
