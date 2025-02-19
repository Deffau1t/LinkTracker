package backend.academy.scrapper.model;

import lombok.Getter;
import java.util.List;

@Getter
public class AddLinkRequest {
    private String link;
    private List<String> tags;
    private List<String> filters;
}
