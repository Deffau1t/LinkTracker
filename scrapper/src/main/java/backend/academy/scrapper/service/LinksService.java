package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class LinksService {

    private Map<Long, List<String>> chatLinks = new HashMap<>();

    public void addLinkOfChat(Long chatId, LinkResponse linkResponse) {
        chatLinks.computeIfAbsent(chatId, _ -> new ArrayList<>()).add(linkResponse.url());
    }

    public void deleteLinkOfChat(Long chatId, RemoveLinkRequest removeLinkRequest) {
        if (chatLinks.containsKey(chatId)) {
            chatLinks.get(chatId).remove(removeLinkRequest.link());
        }
    }
}
