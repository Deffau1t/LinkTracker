package backend.academy.scrapper.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ListLinksResponse - данные о ссылках для ответа.
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
    private List<LinkResponse> links;

    /**
     * size - количество ссылок.
     */
    private int size;
}
