package backend.academy.scrapper.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AddLinkRequest - DTO для добавления ссылки на страницу с новостью.
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddLinkRequest {
    /**
     * Ссылка на страницу с новостью.
     */
    private String link;

    /**
     * Список тегов.
     */
    private List<String> tags;

    /**
     * Список фильтров.
     */
    private List<String> filters;
}
