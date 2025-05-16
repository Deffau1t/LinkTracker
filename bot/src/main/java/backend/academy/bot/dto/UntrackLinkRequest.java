package backend.academy.bot.dto;

/**
 * UntrackLinkRequest - DTO для запроса на отслеживание ссылки.
 * @param link - Ссылка для отслеживания.
 */
public record UntrackLinkRequest(String link) { }
