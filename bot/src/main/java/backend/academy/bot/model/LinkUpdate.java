package backend.academy.bot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkUpdate {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("url")
    private String url;

    @JsonProperty("description")
    private String description;

    @JsonProperty("tg_chat_ids")  // Должно совпадать с JSON полем!
    private List<Long> tgChatIds;
}
