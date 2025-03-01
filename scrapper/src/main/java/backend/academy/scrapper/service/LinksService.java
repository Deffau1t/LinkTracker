package backend.academy.scrapper.service;

import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.StackOverflowClient;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinksService {

    private final Map<Long, List<String>> chatLinks = new HashMap<>();
    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;

    public void addLinkOfChat(Long chatId, LinkResponse linkResponse) {
        chatLinks.computeIfAbsent(chatId, _ -> new ArrayList<>()).add(linkResponse.url());
        checkLinkForUpdates(linkResponse.url());
    }

    public void deleteLinkOfChat(Long chatId, RemoveLinkRequest removeLinkRequest) {
        if (chatLinks.containsKey(chatId)) {
            chatLinks.get(chatId).remove(removeLinkRequest.link());
        }
    }

    private void checkLinkForUpdates(String url) {
        if (isGitHubLink(url)) {
            fetchGitHubUpdates(url);
        } else if (isStackOverflowLink(url)) {
            fetchStackOverflowUpdates(url);
        }
    }

    private boolean isGitHubLink(String url) {
        return url.contains("github.com");
    }

    private boolean isStackOverflowLink(String url) {
        return url.contains("stackoverflow.com");
    }

    private void fetchGitHubUpdates(String url) {
        Pattern pattern = Pattern.compile("https://github.com/([^/]+)/([^/]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            String owner = matcher.group(1);
            String repo = matcher.group(2);
            gitHubClient.fetchRepositoryInfo(owner, repo)
                    .subscribe(response -> {
                        log.info("GitHub repository {} last updated at: {}", response.fullName(), response.updatedAt());
                        // Здесь можно добавить логику для уведомления пользователя
                    }, error -> log.error("Error fetching GitHub repository info: {}", error.getMessage()));
        }
    }

    private void fetchStackOverflowUpdates(String url) {
        Pattern pattern = Pattern.compile("https://stackoverflow.com/questions/(\\d+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            Long questionId = Long.parseLong(matcher.group(1));
            stackOverflowClient.fetchQuestionInfo(questionId)
                    .subscribe(response -> {
                        log.info("StackOverflow question {} last activity at: {}",
                            response.questionId(), response.lastActivityDate());
                        // Здесь можно добавить логику для уведомления пользователя
                    }, error -> log.error("Error fetching StackOverflow question info: {}", error.getMessage()));
        }
    }
}
