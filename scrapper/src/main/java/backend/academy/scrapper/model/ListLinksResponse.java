package backend.academy.scrapper.model;

import lombok.Getter;
import java.util.List;

@Getter
public class ListLinksResponse {
    private List<LinkResponse> links;
    private int size;
}
