package backend.academy.bot.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * LinkUpdate model.
 */

@Getter
@Builder
public class LinkUpdate {
    /**
     * Уникальный идентификатор ссылки.
     */
    private Long id;

    /**
     * URL ссылки.
     */
    private String url;

    /**
     * Описание ссылки.
     */
    private String description;

    /**
     * Список идентификаторов чатов Telegram, которые отслеживают эту ссылку.
     */
    private List<Long> tgChatIds;
}
