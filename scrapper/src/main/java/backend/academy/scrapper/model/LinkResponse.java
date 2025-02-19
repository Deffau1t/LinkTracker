package backend.academy.scrapper.model;

import lombok.Getter;
import java.util.List;

@Getter
public class LinkResponse {
    private Long id;
    private String url;
    private List<String> tags;
    private List<String> filters;
}
