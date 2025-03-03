package backend.academy.scrapper.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * LinkUpdate - дто для обновления ссылки.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkUpdate {
    /**
     * Id - идентификатор ссылки.
     */
    @JsonProperty("id")
    private Long id;

    /**
     * url - ссылка.
     */
    @JsonProperty("url")
    private String url;

    /**
     * description - описание ссылки.
     */
    @JsonProperty("description")
    private String description;

    /**
     * tgChatIds - список идентификаторов чатов Telegram,
     * которые отслеживают эту ссылку.
     */
    @JsonProperty("tgChatIds")
    private List<Long> tgChatIds;
}
