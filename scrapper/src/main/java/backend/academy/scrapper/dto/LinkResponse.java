package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * LinkResponse - дто для ответа по запросу на получение ссылки по ID.
 */

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class LinkResponse {
    /**
     * Id - идентификатор ссылки.
     */
    @JsonProperty(value = "id", required = true)
    private Long id;

    /**
     * url - ссылка.
     */
    @JsonProperty(value = "url", required = true)
    private String url;

    /**
     * tags - теги.
     */
    @JsonProperty(value = "tags", required = true)
    private List<String> tags;

    /**
     * filters - фильтры.
     */
    @JsonProperty(value = "filters", required = true)
    private List<String> filters;
}
