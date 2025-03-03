package backend.academy.bot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TrackedLink представляет собой объект,
 * который содержит информацию об отслеживаемой ссылке.
 */

@Getter
@AllArgsConstructor
public class TrackedLink {
    /**
     * URL отслеживаемой ссылки.
     */
    private final String url;

    /**
     * Теги отслеживаемой ссылки.
     */
    private final String tags;

    /**
     * Фильтры отслеживаемой ссылки.
     */
    private final String filters;
}
