package backend.academy.scrapper.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Repository;

/**
 * LinkTrackingRepository - класс для отслеживания ссылок пользователей.
 */

@Repository
public class LinkTrackingRepository {
    /**
     * trackedLinks - отслеживаемые ссылки пользователей.
     */
    private final Map<String, List<Long>> trackedLinks = new HashMap<>();

    /**
     * trackLink - метод для отслеживания ссылок пользователей.
     * @param chatId - id чата
     * @param link - ссылка
     */
    public void trackLink(final Long chatId, final String link) {
        trackedLinks.computeIfAbsent(
            link, _ -> new ArrayList<>()
        ).add(chatId);
    }

    /**
     * untrackLink - метод для отслеживания ссылок пользователей.
     * @param chatId - id чата
     * @param link - ссылка
     */
    public void untrackLink(final Long chatId, final String link) {
        trackedLinks.getOrDefault(link, new ArrayList<>()).remove(chatId);
        if (trackedLinks.getOrDefault(link, List.of()).isEmpty()) {
            trackedLinks.remove(link);
        }
    }

    /**
     * getAllTrackedLinks - метод для получения всех отслеживаемых ссылок.
     * @return - список отслеживаемых ссылок
     */
    public Set<String> getAllTrackedLinks() {
        return trackedLinks.keySet();
    }

    /**
     * getChatIdsForLink - метод для получения id чатов для указанной ссылки.
     * @param link - ссылка
     * @return - список id чатов
     */
    public List<Long> getChatIdsForLink(final String link) {
        return trackedLinks.getOrDefault(link, List.of());
    }
}
