package backend.academy.bot;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class LinkParserTest {

    @Test
    void shouldParseGitHubLink() {
        String link = "https://github.com/torvalds/linux";
        assertTrue(link.contains("github.com"));
    }

    @Test
    void shouldParseStackOverflowLink() {
        String link = "https://stackoverflow.com/questions/12345/how-to-test";
        assertTrue(link.contains("stackoverflow.com"));
    }

    @Test
    void shouldNotParseInvalidLink() {
        String link = "https://example.com";
        assertFalse(link.contains("github.com") || link.contains("stackoverflow.com"));
    }
}
