package backend.academy.scrapper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * GitHubRepository - модель данных для ответа на запрос
 * списка репозиториев пользователя на GitHub.
 */

@Getter
public class GitHubRepository {
    /**
     * updatedAt - дата последнего обновления репозитория.
     */
    @JsonProperty("updated_at")
    private String updatedAt;
}
