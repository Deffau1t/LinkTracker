package backend.academy.scrapper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

@Getter
public class GitHubRepository {
    @JsonProperty("updated_at")
    private String updatedAt;
}
