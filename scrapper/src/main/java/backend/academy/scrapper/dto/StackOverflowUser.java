package backend.academy.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * StackOverflowUser - класс для представления пользователя StackOverflow.
 * @param displayName
 */
public record StackOverflowUser(
    @JsonProperty("display_name") String displayName
) {
}
