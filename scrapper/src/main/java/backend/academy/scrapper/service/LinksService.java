package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import java.util.List;

public interface LinksService {
    void registerChat(Long chatId);
    void deleteChat(Long chatId);
    void addLink(Long chatId, LinkResponse linkResponse);
    void removeLink(Long chatId, RemoveLinkRequest removeLinkRequest);
    List<LinkUpdate> getAllLinks(Long chatId);
}
