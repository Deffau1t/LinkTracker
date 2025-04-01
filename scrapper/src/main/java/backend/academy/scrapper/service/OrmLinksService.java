package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.entity.LinkUpdate;
import backend.academy.scrapper.repository.LinksRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "database.access-type", havingValue = "ORM", matchIfMissing = true)
public class OrmLinksService implements LinksService {
    private final LinksRepository linksRepository;

    @Override
    public void addLink(Long chatId, LinkResponse linkResponse) {
        String url = linkResponse.url();
        List<String> tags = linkResponse.tags();
        List<String> filters = linkResponse.filters();
        linksRepository.addLink(chatId, url, tags, filters);
    }

    @Override
    public void removeLink(Long chatId, RemoveLinkRequest removeLinkRequest) {
        String url = removeLinkRequest.link();
        linksRepository.removeLink(chatId, url);
    }

    @Override
    public Optional<LinkUpdate> findByUrl(String url) {
        return linksRepository.findByUrl(url);
    }

}
