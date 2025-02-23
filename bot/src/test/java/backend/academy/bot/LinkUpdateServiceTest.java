package backend.academy.bot;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import backend.academy.bot.service.LinkUpdateService;
import backend.academy.bot.service.LinkTrackerBot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

class LinkUpdateServiceTest {
    private LinkUpdateService linkUpdateService;
    private LinkTrackerBot botMock;

    @BeforeEach
    void setup() {
        botMock = mock(LinkTrackerBot.class);
        linkUpdateService = new LinkUpdateService();
        linkUpdateService.linkTrackerBot(botMock);
    }

    @Test
    void testTrackCommand_ShouldAddLink() {
        long chatId = 123L;
        String link = "https://example.com";

        linkUpdateService.trackCommand(chatId, link);

        assertTrue(linkUpdateService.chatSubscribes().containsKey(chatId));
        assertTrue(linkUpdateService.chatSubscribes().get(chatId).contains(link));
    }

    @Test
    void testUntrackCommand_ShouldRemoveLink() {
        long chatId = 123L;
        String link = "https://example.com";

        linkUpdateService.trackCommand(chatId, link);
        linkUpdateService.untrackCommand(chatId, link);

        assertFalse(linkUpdateService.chatSubscribes().get(chatId).contains(link));
    }
}
