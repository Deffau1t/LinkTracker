//package backend.academy.bot;
//
//import backend.academy.bot.service.LinkTrackerBot;
//import backend.academy.bot.service.LinkUpdateService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.mock;
//
//class LinkUpdateServiceTest {
//    private LinkUpdateService linkUpdateService;
//    private LinkTrackerBot botMock;
//
//    @BeforeEach
//    void setup() {
//        botMock = mock(LinkTrackerBot.class);
//        linkUpdateService = new LinkUpdateService();
//        linkUpdateService.linkTrackerBot(botMock);
//    }
//
//    @Test
//    void testTrackCommand_ShouldAddLink() {
//        long chatId = 123L;
//        String link = "https://example.com";
//
//        linkUpdateService.trackCommand(chatId, link);
//
//        assertTrue(linkUpdateService.chatSubscribes().containsKey(chatId));
//        assertTrue(linkUpdateService.chatSubscribes().get(chatId).contains(link));
//    }
//
//    @Test
//    void testUntrackCommand_ShouldRemoveLink() {
//        long chatId = 123L;
//        String link = "https://example.com";
//
//        linkUpdateService.trackCommand(chatId, link);
//        linkUpdateService.untrackCommand(chatId, link);
//
//        assertFalse(linkUpdateService.chatSubscribes().get(chatId).contains(link));
//    }
//}
