package backend.academy.scrapper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * RemoveLinkRequest - запрос на удаление ссылки.
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RemoveLinkRequest {
    /**
     * Ссылка на страницу с новостью.
     */
    private String link;
}
