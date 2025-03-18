package backend.academy.scrapper;

import backend.academy.scrapper.repository.LinkTrackingRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

class LinkTrackingRepositoryTest {

    private final LinkTrackingRepository repository = new LinkTrackingRepository();

    @Test
    void shouldSaveAndRetrieveTrackedLinks() {
        repository.trackLink(1L, "https://github.com/test/repo");

        Set<String> trackedLinks = repository.getAllTrackedLinks();
        assertTrue(trackedLinks.contains("https://github.com/test/repo"));
    }
}
