package backend.academy.scrapper.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class LinkUpdate {
    private Long id;
    private String url;
    private String description;
    private List<Long> tgChatIds;
}
