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
    private Long id;
    private String url;
    private String description;
    private List<Long> tgChatIds;
}
