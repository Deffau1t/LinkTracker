package backend.academy.bot.model;

import java.util.List;

/**
 * TrackLinkRequest - Класс для хранения данных о запросе
 * на добавление сайта для отслеживания.
 * @param link - Ссылка на сайт.
 * @param tags - Теги сайта.
 * @param filters - Фильтры сайта.
 */
public record TrackLinkRequest(
    String link,
    List<String> tags,
    List<String> filters) { }
