package backend.academy.scrapper;

import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.StackOverflowClient;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.repository.LinkTrackingRepository;
import backend.academy.scrapper.service.LinksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LinkTrackingServiceTest {

    private LinksService linksService;
    private LinkTrackingRepository mockRepository;
    private GitHubClient mockGitHubClient;
    private StackOverflowClient mockStackOverflowClient;

    @BeforeEach
    void setUp() {
        mockRepository = Mockito.mock(LinkTrackingRepository.class);
        mockGitHubClient = Mockito.mock(GitHubClient.class);
        mockStackOverflowClient = Mockito.mock(StackOverflowClient.class);
        linksService = new LinksService(mockGitHubClient, mockStackOverflowClient, mockRepository);
    }

    @Test
    void shouldUntrackLink() {
        RemoveLinkRequest request = new RemoveLinkRequest("https://github.com/test/repo");

        linksService.deleteLinkOfChat(1L, request);

        verify(mockRepository, times(1)).untrackLink(1L, "https://github.com/test/repo");
    }

    @Test
    void shouldFetchGitHubUpdates() {
        String url = "https://github.com/test/repo";
        when(mockGitHubClient.fetchRepositoryInfo("test", "repo")).thenReturn(mock(Mono.class));

        linksService.fetchGitHubUpdates(url);

        verify(mockGitHubClient, times(1)).fetchRepositoryInfo("test", "repo");
    }

    @Test
    void shouldFetchStackOverflowUpdates() {
        String url = "https://stackoverflow.com/questions/12345";
        when(mockStackOverflowClient.fetchQuestionInfo(12345L)).thenReturn(mock(Mono.class));

        linksService.fetchStackOverflowUpdates(url);

        verify(mockStackOverflowClient, times(1)).fetchQuestionInfo(12345L);
    }
}

