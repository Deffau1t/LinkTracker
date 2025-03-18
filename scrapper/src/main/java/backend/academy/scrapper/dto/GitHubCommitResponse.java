package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

/**
 * GitHubCommitResponse - класс для работы с данными коммита
 * к GitHubIssueResponse.
 * @param commit
 */

public record GitHubCommitResponse(
    @JsonProperty("commit") Commit commit
) {

    /**
     * Commit - класс для работы с данными Commit.
     * @param message - сообщение коммита.
     * @param author - автор коммита.
     */
    public record Commit(
        @JsonProperty("message") String message,
        @JsonProperty("author") Author author
    ) {

    }

    /**
     * Author - класс для работы с данными Author.
     * @param date - дата автора коммита.
     */
    public record Author(
        @JsonProperty("date") Instant date
    ) {

    }
}
