package backend.academy.bot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkUpdateDTO {
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
     * tags - теги ссылки.
     */
    @JsonProperty("tags")
    private List<String> tags;

    /**
     * filters - фильтры ссылки.
     */
    @JsonProperty("filters")
    private List<String> filters;

    /**
     * tg_chat_ids - ID чатов для уведомлений.
     */
    @JsonProperty("tg_chat_ids")
    private List<Long> tgChatIds;
}
