package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;

public interface LinksService {
    void addLink(Long chatId, LinkResponse linkResponse);
    void removeLink(Long chatId, RemoveLinkRequest removeLinkRequest);
}
