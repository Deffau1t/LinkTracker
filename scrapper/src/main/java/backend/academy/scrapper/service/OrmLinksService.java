package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.entity.LinkUpdate;
import backend.academy.scrapper.repository.LinksRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "database.access-type", havingValue = "ORM", matchIfMissing = true)
public class OrmLinksService implements LinksService {
    private final LinksRepository linksRepository;

    @Override
    public void registerChat(Long chatId) {
        linksRepository.registerChat(chatId);
    }

    @Override
    public void deleteChat(Long chatId) {
        linksRepository.deleteChat(chatId);
    }

    @Override
    public void addLink(Long chatId, LinkResponse linkResponse) {
        String url = linkResponse.url();
        String description = String.join(", ", linkResponse.tags());
        linksRepository.addLink(chatId, url, description);
    }

    @Override
    public void removeLink(Long chatId, RemoveLinkRequest removeLinkRequest) {
        String url = removeLinkRequest.link();
        linksRepository.removeLink(chatId, url);
    }

    @Override
    public List<LinkUpdate> getAllLinks(Long chatId) {
        return linksRepository.getAllLinks(chatId);
    }

    @Override
    public List<Long> getAllChatIds() {
        return linksRepository.getAllChatIds();
    }
}
