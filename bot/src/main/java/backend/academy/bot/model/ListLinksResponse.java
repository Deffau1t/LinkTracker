package backend.academy.bot.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListLinksResponse {
    @JsonProperty("links")
    private List<LinkResponse> links;

    @JsonProperty("size")
    private int size;

    public void setLinks(List<LinkResponse> links) {
        this.links = links;
        this.size = (links != null) ? links.size() : 0;
    }
}
