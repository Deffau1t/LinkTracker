package backend.academy.bot.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LinkUpdate {
    private Long id;
    private String url;
    private String description;
    private List<Long> tgChatIds;
}
