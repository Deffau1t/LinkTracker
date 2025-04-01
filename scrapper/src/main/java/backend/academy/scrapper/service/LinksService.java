package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.entity.LinkUpdate;
import java.util.Optional;

public interface LinksService {
    void addLink(Long chatId, LinkResponse linkResponse);
    void removeLink(Long chatId, RemoveLinkRequest removeLinkRequest);
    Optional<LinkUpdate> findByUrl(String url);
}
