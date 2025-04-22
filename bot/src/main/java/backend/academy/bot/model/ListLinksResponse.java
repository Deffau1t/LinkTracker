package backend.academy.bot.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ListLinksResponse - список ссылок.
 */

@Getter
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
