package backend.academy.bot.model;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LinkUpdate {
    private Long id;
    private String url;
    private String description;
    private List<Long> tgChatIds;
}
