package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ListLinksResponse - модель ответа от сервера при запросе списка ссылок.
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListLinksResponse {
    /**
     * links - список ссылок.
     */
    @JsonProperty("links")
    private List<LinkResponse> links;

    /**
     * size - размер списка.
     */
    @JsonProperty("size")
    private int size;
}
