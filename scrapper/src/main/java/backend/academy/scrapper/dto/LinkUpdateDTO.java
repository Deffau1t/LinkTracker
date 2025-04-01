package backend.academy.scrapper.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для передачи данных об обновлениях ссылок в бот.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkUpdateDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("url")
    private String url;

    @JsonProperty("description")
    private String description;

    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("filters")
    private List<String> filters;

    @JsonProperty("tg_chat_ids")
    private List<Long> tgChatIds;
}
