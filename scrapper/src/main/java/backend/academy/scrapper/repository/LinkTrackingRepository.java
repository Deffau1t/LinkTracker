package backend.academy.scrapper.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class LinkTrackingRepository {
    private final Map<String, List<Long>> trackedLinks = new HashMap<>();

    public void trackLink(Long chatId, String link) {
        trackedLinks.computeIfAbsent(link, _ -> new ArrayList<>()).add(chatId);
    }

    public void untrackLink(Long chatId, String link) {
        trackedLinks.getOrDefault(link, new ArrayList<>()).remove(chatId);
        if (trackedLinks.getOrDefault(link, List.of()).isEmpty()) {
            trackedLinks.remove(link);
        }
    }

    public Set<String> getAllTrackedLinks() {
        return trackedLinks.keySet();
    }

    public List<Long> getChatIdsForLink(String link) {
        return trackedLinks.getOrDefault(link, List.of());
    }
}
