package backend.academy.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * LinkUpdate - класс для обновления ссылок в базе данных.
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkUpdate {
    /**
     * Id - id ссылки.
     */
    @JsonProperty("id")
    private Long id;

    /**
     * Url - ссылка.
     */
    @JsonProperty("url")
    private String url;

    /**
     * Description - описание ссылки.
     */
    @JsonProperty("description")
    private String description;

    /**
     * Tg_chat_ids - список чатов в которых нужно отправить ссылку.
     */
    @JsonProperty("tg_chat_ids")
    private List<Long> tgChatIds;
}
