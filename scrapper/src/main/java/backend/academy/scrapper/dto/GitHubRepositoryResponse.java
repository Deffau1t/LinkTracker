package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class GitHubRepositoryResponse {
    private Long id;
    private String name;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;
}
