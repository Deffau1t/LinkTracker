package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubPullRequestResponse(
    @JsonProperty("id") Long id,
    @JsonProperty("title") String title,
    @JsonProperty("user") GitHubUser user,
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    String createdAt,
    @JsonProperty("body") String body,
    @JsonProperty("state") String state,
    @JsonProperty("html_url") String htmlUrl
) {
}
