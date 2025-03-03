package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * GitHubRepositoryResponse - для работы с данными GitHubRepositoryResponse.
 */

@Getter
@Setter
public class GitHubRepositoryResponse {
    /**
     * id - id репозитория.
     */
    private Long id;

    /**
     * name - имя репозитория.
     */
    @JsonProperty("name")
    private String name;

    /**
     * fullName - полное имя репозитория.
     */
    @JsonProperty("full_name")
    private String fullName;

    /**
     * htmlUrl - ссылка на репозиторий.
     */
    @JsonProperty("html_url")
    private String htmlUrl;

    /**
     * updatedAt - дата последнего обновления репозитория.
     */
    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;
}
