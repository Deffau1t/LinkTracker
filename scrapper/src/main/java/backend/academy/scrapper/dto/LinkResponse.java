package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkResponse {
    private Long id;
    private String url;
    private List<String> tags;
    private List<String> filters;
}
